package org.qpneruy.clashArena.ArenaManager;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.qpneruy.clashArena.ArenaManager.worldManager.Schematic.SchematicPasterManager;
import org.qpneruy.clashArena.ArenaManager.worldManager.WorldManager;
import org.qpneruy.clashArena.ClashArena;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class ArenaManager {

    private final WorldManager worldManager;
    private final SchematicPasterManager schematicManager;
    private final File schematicsFolder;

    private final Map<String, ArenaInfo> arenas = new ConcurrentHashMap<>();

    private final long ARENA_IDLE_TIMEOUT_MS = TimeUnit.MINUTES.toMillis(30);
    private final long CLEANUP_INTERVAL_MS = TimeUnit.MINUTES.toMillis(5);

    private final ExecutorService asyncExecutor = Executors.newCachedThreadPool(
            r -> new Thread(r, "Arena-Worker-Thread"));
    private final ScheduledExecutorService cleanupExecutor;

    public ArenaManager(WorldManager worldManager, SchematicPasterManager schematicManager) {
        this.worldManager = worldManager;
        this.schematicManager = new SchematicPasterManager(); //Just for testing, should be injected
        this.schematicsFolder = new File(ClashArena.instance.getDataFolder().getAbsolutePath(), "Schematics");

        if (!schematicsFolder.isDirectory())
            log.warn("Schematics folder does not exist or is not a directory: {}", schematicsFolder.getAbsolutePath());


        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "Arena-Cleanup-Thread");
            thread.setDaemon(true);
            return thread;
        });

        startCleanupTask();
        log.info("ArenaManager initialized. Cleanup task scheduled.");
    }

    public CompletableFuture<ArenaCreationResult> createArena(Player owner, String schematicName) {
        String arenaId = null;
        try {
            arenaId = worldManager.createWorld(owner);
            World world = worldManager.getWorld(arenaId);
            if (world == null) {
                log.error("WorldManager failed to create or return world for id: {}", arenaId);
                return CompletableFuture.completedFuture(ArenaCreationResult.creationFailure("World creation failed"));
            }

            final String finalArenaId = arenaId;
            Location spawnLocation = world.getSpawnLocation();
            File schematicFile = new File(schematicsFolder, schematicName + ".schem");

            if (!schematicFile.exists()) {
                log.error("Schematic file not found: {}", schematicFile.getAbsolutePath());
                cleanupFailedArenaCreation(finalArenaId);
                return CompletableFuture.completedFuture(ArenaCreationResult.creationFailure("Schematic not found: " + schematicName));
            }

            arenas.put(finalArenaId, new ArenaInfo(world, ArenaState.CREATING));
            log.info("Arena {} CREATING state set. Starting paste...", finalArenaId);

            return schematicManager.getSchematicPaster()
                    .paste(schematicFile, spawnLocation, true)
                    .thenApplyAsync(unused -> {
                        ArenaInfo info = arenas.get(finalArenaId);
                        if (info != null) {
                            info.setState(ArenaState.READY);
                            info.updateLastUsed();
                            log.info("Arena {} successfully created and set to READY state.", finalArenaId);
                            return ArenaCreationResult.success(finalArenaId, spawnLocation);
                        } else {
                            log.warn("ArenaInfo for {} was null after successful paste. Potential race condition or premature cleanup?", finalArenaId);
                            cleanupFailedArenaCreation(finalArenaId);
                            return ArenaCreationResult.failure(finalArenaId, "Arena state lost after paste");
                        }
                    }, asyncExecutor)
                    .exceptionally(ex -> {
                        log.error("Failed to paste schematic for arena {}: {}", finalArenaId, ex.getMessage(), ex);
                        cleanupFailedArenaCreation(finalArenaId);
                        return ArenaCreationResult.failure(finalArenaId, "Schematic pasting failed: " + ex.getMessage());
                    });

        } catch (Exception e) {
            log.error("Unexpected synchronous error during arena creation setup for schematic '{}': {}", schematicName, e.getMessage(), e);
            if (arenaId != null) {
                cleanupFailedArenaCreation(arenaId);
            }
            return CompletableFuture.completedFuture(ArenaCreationResult.creationFailure("Initial setup error: " + e.getMessage()));
        }
    }


    public void markArenaActive(String arenaId) {
        ArenaInfo info = arenas.get(arenaId);
        if (info != null) {
            info.setState(ArenaState.IN_USE);
            info.updateLastUsed();
             log.debug("Arena {} marked as IN_USE.", arenaId); // Optional: debug logging
        } else {
            log.warn("Attempted to mark non-existent or already cleaned up arena {} as active.", arenaId);
        }
    }

    /**
     * Marks an arena as ready (idle), resetting its idle timer.
     * Typically called when a match ends, but the arena shouldn't be deleted immediately.
     *
     * @param arenaId The ID of the arena.
     */
    public void markArenaReady(String arenaId) {
        ArenaInfo info = arenas.get(arenaId);
        if (info != null && info.getState() == ArenaState.IN_USE) {
            info.setState(ArenaState.READY);
            info.updateLastUsed();
             log.debug("Arena {} marked as READY.", arenaId); // Optional: debug logging
        } else if (info != null) {
            log.warn("Attempted to mark arena {} as READY, but it was not IN_USE (state: {}).", arenaId, info.getState());
        } else {
            log.warn("Attempted to mark non-existent or already cleaned up arena {} as ready.", arenaId);
        }
    }

    public CompletableFuture<Boolean> deleteArena(String arenaId) {
        ArenaInfo info = arenas.get(arenaId);
        if (info == null) {
            log.warn("Attempted to delete non-existent arena: {}", arenaId);
            return CompletableFuture.completedFuture(false);
        }

        return CompletableFuture.supplyAsync(() -> {
            log.info("Manual deletion requested for arena: {}", arenaId);
            return performArenaDeletion(arenaId, info);
        }, asyncExecutor);
    }

    private boolean performArenaDeletion(String arenaId, ArenaInfo info) {
        if (arenas.remove(arenaId) == null) {
            log.warn("Arena {} was already removed when performArenaDeletion was called.", arenaId);
            return false;
        }

        log.info("Performing deletion for arena: {}", arenaId);
        try {
            World world = info.getWorld();
            if (world != null) {
                teleportPlayersToSafety(world);
                if (worldManager.deleteWorld(arenaId)) {
                    log.error("WorldManager failed to delete world for arena: {}", arenaId);
                    return false;
                }
            }

            log.info("Successfully deleted arena: {}", arenaId);
            return true;
        } catch (Exception e) {
            log.error("Error during deletion of arena {}: {}", arenaId, e.getMessage(), e);
            return false;
        }
    }


    /**
     * Cleans up resources if arena creation fails *before* pasting completes or if pasting fails.
     * Assumes the arena might be in CREATING state or not even fully registered yet.
     */
    private void cleanupFailedArenaCreation(String arenaId) {
        log.warn("Cleaning up failed arena creation for ID: {}", arenaId);
        arenas.remove(arenaId);
        CompletableFuture.runAsync(() -> {
            try {
                worldManager.deleteWorld(arenaId);
                log.info("Cleaned up world for failed arena creation: {}", arenaId);
            } catch (Exception e) {
                log.error("Error cleaning up world for failed arena creation {}: {}", arenaId, e.getMessage(), e);
            }
        }, asyncExecutor);
    }

    private void teleportPlayersToSafety(World world) {
        if (world == null) return;
        List<Player> playersInWorld = new ArrayList<>(world.getPlayers());
        if (playersInWorld.isEmpty()) return;

        World defaultWorld = Bukkit.getWorlds().get(0);
        if (defaultWorld == null) {
            log.error("Cannot teleport players to safety: Default world is null!");
            return;
        }
        Location safeSpawn = defaultWorld.getSpawnLocation();

        log.info("Teleporting {} players from world {} to safety.", playersInWorld.size(), world.getName());
        for (Player player : playersInWorld) {
            Bukkit.getScheduler().runTask(ClashArena.instance, () -> {
                player.teleport(safeSpawn);
                player.sendMessage("§cThe arena you were in is being removed. You have been teleported to safety.");
            });
        }
    }

    private void startCleanupTask() {
        cleanupExecutor.scheduleAtFixedRate(
                this::cleanupIdleArenas,
                CLEANUP_INTERVAL_MS,
                CLEANUP_INTERVAL_MS,
                TimeUnit.MILLISECONDS
        );
    }

    private void cleanupIdleArenas() {
        long currentTime = System.currentTimeMillis();
         log.debug("Running cleanup task...");
        List<Map.Entry<String, ArenaInfo>> arenasToDelete = new ArrayList<>();
        for (Map.Entry<String, ArenaInfo> entry : arenas.entrySet()) {
            ArenaInfo info = entry.getValue();
            if (info.getState() == ArenaState.READY) {
                if ((currentTime - info.getLastUsedTimestamp()) > ARENA_IDLE_TIMEOUT_MS) {
                    log.info("Arena {} marked for cleanup due to idle timeout.", entry.getKey());
                    arenasToDelete.add(entry);
                }
            }
//             else if (info.getState() == ArenaState.CREATING) {
//                 if ((currentTime - info.getLastUsedTimestamp()) > CREATION_TIMEOUT_MS) { // Need another constant
//                     log.warn("Arena {} stuck in the CREATING state. Marking for cleanup.", entry.getKey());
//                     arenasToDelete.add(entry);
//                 }
//             }
        }

        if (!arenasToDelete.isEmpty()) {
            log.info("Cleanup task found {} idle arenas to delete.", arenasToDelete.size());
            for (Map.Entry<String, ArenaInfo> entry : arenasToDelete) {
                performArenaDeletion(entry.getKey(), entry.getValue());
            }
        }
         log.debug("Cleanup task finished.");
    }

    public void shutdown() {
        log.info("Shutting down ArenaManager...");
        cleanupExecutor.shutdown();
        asyncExecutor.shutdown();

        try {
            if (!cleanupExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                log.warn("Cleanup task did not terminate gracefully after 10 seconds. Forcing shutdown.");
                cleanupExecutor.shutdownNow();
            }
            log.info("Deleting all remaining arenas...");
            List<Map.Entry<String, ArenaInfo>> remainingArenas = new ArrayList<>(arenas.entrySet());
            for (Map.Entry<String, ArenaInfo> entry : remainingArenas) {
                performArenaDeletion(entry.getKey(), entry.getValue());
            }
            if (!asyncExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                log.warn("Async worker tasks did not terminate gracefully after 30 seconds. Forcing shutdown.");
                asyncExecutor.shutdownNow();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("ArenaManager shutdown interrupted.", e);
            cleanupExecutor.shutdownNow();
            asyncExecutor.shutdownNow();
        } catch (Exception e) {
            log.error("Error during ArenaManager shutdown.", e);
        } finally {
            arenas.clear();
            log.info("ArenaManager shutdown complete.");
        }
    }
}