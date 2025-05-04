package org.qpneruy.clashArena.ArenaManager.worldManager;

import lombok.extern.slf4j.Slf4j;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.qpneruy.clashArena.ArenaManager.worldManager.chunkProvider.VoidChunkGenerator;
import org.qpneruy.clashArena.ClashArena;
import org.qpneruy.clashArena.utils.game.CRC32CHash;

import java.io.File;

import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class WorldManager {
    private final ConcurrentHashMap<String, World> worldMap = new ConcurrentHashMap<>();

    public String createWorld(Player worldOwner) {
        String sessionId = CRC32CHash.CRC32C(String.valueOf(worldOwner.getUniqueId()));
        String worldPath = "MatchWorlds/" + sessionId;

        WorldCreator creator = new WorldCreator(worldPath);

        creator.environment(World.Environment.NORMAL);
        creator.type(WorldType.FLAT);
        creator.generator(new VoidChunkGenerator());

        worldMap.put(sessionId, creator.createWorld());
        return sessionId;
    }

    public boolean deleteWorld(String sessionId) {
        World world = worldMap.remove(sessionId);
        if (world != null) {
            Runnable deleteTask = () -> {
                try {
                    world.save();
                    for (Chunk chunk : world.getLoadedChunks()) chunk.unload(true);

                    if (Bukkit.unloadWorld(world, true)) {
                        File worldFolder = world.getWorldFolder();
                        if (deleteDirectory(worldFolder)) {
                            log.info("Successfully deleted world: {}", sessionId);
                        } else {
                            log.warn("Failed to delete world folder for: {}", sessionId);
                        }
                    } else {
                        log.warn("Failed to unload world: {}", sessionId);
                    }
                } catch (Exception e) {
                    log.error("Error deleting world: {}", sessionId, e);
                }
            };

            if (Bukkit.isPrimaryThread()) {
                deleteTask.run();
            } else {
                Bukkit.getScheduler().runTask(ClashArena.instance, deleteTask);
            }
        }
        return true;
    }

    public World getWorld(String sessionId) {
        return worldMap.get(sessionId);
    }

    private boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        if (!file.delete()) {
                            log.warn("Failed to delete file: {}", file.getPath());
                        }
                    }
                }
            }
            return directory.delete();
        }
        return false;
    }
}
