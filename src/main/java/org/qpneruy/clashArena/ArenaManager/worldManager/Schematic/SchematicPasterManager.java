package org.qpneruy.clashArena.ArenaManager.worldManager.Schematic;

import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.extension.platform.Platform;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.qpneruy.clashArena.ClashArena;
import org.qpneruy.clashArena.utils.ClashArenaLogger;
import org.qpneruy.clashArena.ArenaManager.worldManager.Schematic.paster.FawePaster;
import org.qpneruy.clashArena.ArenaManager.worldManager.Schematic.paster.SchematicPaster;
import org.qpneruy.clashArena.ArenaManager.worldManager.Schematic.paster.WorldEditPaster;

import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Level;

public class SchematicPasterManager implements AutoCloseable {
    private final ExecutorService executorService =  Executors.newVirtualThreadPerTaskExecutor();

    private final boolean worldEditEnabled;
    private final boolean faweEnabled;
    private static int LIMIT_PER_TICK = 10;
    private static int DELAY_TICK = 1;

    @Getter
    private SchematicPaster schematicPaster;

    public SchematicPasterManager() {
        this.worldEditEnabled = Bukkit.getPluginManager().isPluginEnabled("WorldEdit");
        this.faweEnabled = Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit");

        initializePasters();
    }

    public static boolean isWorldEditFunctional() {
        try {
            Platform platform = com.sk89q.worldedit.WorldEdit.getInstance()
                    .getPlatformManager()
                    .queryCapability(Capability.WORLD_EDITING);
            return platform.getDataVersion() >= 0;
        } catch (Throwable t) {
            ClashArenaLogger.debug(ClashArenaLogger.debugLevel(), Level.WARNING, "WorldEdit platform check failed. It might be incompatible or not fully initialized: " + t.getMessage());
            return false;
        }
    }


    private void initializePasters() {
        Map<String, SchematicPaster> availablePasters = new ConcurrentHashMap<>();
        boolean worldEditFunctional = isWorldEditFunctional();

        if (worldEditFunctional) {
            if (this.worldEditEnabled && !this.faweEnabled) {
                availablePasters.put("worldedit", new WorldEditPaster(this.executorService));
                ClashArenaLogger.info("Using WorldEdit for schematics.");
            }
            if (this.faweEnabled) {
                availablePasters.put("fawe", new FawePaster(this.executorService));
                ClashArenaLogger.info("Using FastAsyncWorldEdit (FAWE) for schematics.");
            }
        } else {
            ClashArenaLogger.warn("WorldEdit platform not functional. Schematic pasting might not work correctly.");
        }
        checkPasterCompatibility(worldEditFunctional);

        setPasterFromConfig(availablePasters);

        if (this.schematicPaster == null && !availablePasters.isEmpty()) {
            ClashArenaLogger.warn("No suitable paster found or configured. Falling back to the first available one.");
            this.schematicPaster = availablePasters.values().iterator().next();
        } else if (this.schematicPaster == null) {
            ClashArenaLogger.error("FATAL: No schematic paster available or configured. Schematic features will be disabled.");
        }
    }

    private void checkPasterCompatibility(boolean worldEditFunctional) {
        if (!worldEditFunctional) {
            if (this.worldEditEnabled) {
                ClashArenaLogger.warn("WorldEdit is enabled, but its platform check failed. Check for version compatibility issues.");
            }
            if (this.faweEnabled) {
                ClashArenaLogger.warn("FAWE is enabled, but the underlying WorldEdit platform check failed. Check for version compatibility issues.");
            }
        }
    }

    private void setPasterFromConfig(Map<String, SchematicPaster> availablePasters) {
        ConfigurationSection schematicConfig = ClashArena.instance.getConfig()
                .getConfigurationSection("Schematic");

        String preferredPasterKey = "fawe"; // Default preference
        if (schematicConfig != null) {
            preferredPasterKey = schematicConfig.getString("paster", preferredPasterKey);
             SchematicPasterManager.LIMIT_PER_TICK = schematicConfig.getInt("LimitPerTick", LIMIT_PER_TICK);
             SchematicPasterManager.DELAY_TICK = schematicConfig.getInt("Delay", DELAY_TICK);
        } else {
            ClashArenaLogger.info("Schematic configuration section not found, using default paster preference ('fawe').");
        }


        this.schematicPaster = availablePasters.get(preferredPasterKey.toLowerCase());

        if (this.schematicPaster == null) {
            ClashArenaLogger.warn("Configured paster '" + preferredPasterKey + "' is not available.");
            this.schematicPaster = availablePasters.get("fawe");
            if (this.schematicPaster == null) {
                this.schematicPaster = availablePasters.get("worldedit");
                if (this.schematicPaster != null) {
                    ClashArenaLogger.info("Falling back to available paster: worldedit");
                }
            } else {
                ClashArenaLogger.info("Falling back to available paster: fawe");
            }
        } else {
            ClashArenaLogger.info("Using configured schematic paster: " + preferredPasterKey);
        }
    }


    @Override
    public void close() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                ClashArenaLogger.warn("Executor service did not terminate gracefully, forcing shutdown.");
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            ClashArenaLogger.warn("Interrupted while waiting for executor service termination, forcing shutdown.");
            Thread.currentThread().interrupt();
        }
    }
}