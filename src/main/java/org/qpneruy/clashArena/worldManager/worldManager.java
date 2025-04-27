package org.qpneruy.clashArena.worldManager;

import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.qpneruy.clashArena.utils.ClashArenaLogger;
import org.qpneruy.clashArena.utils.game.CRC32CHash;
import org.qpneruy.clashArena.worldManager.chunkProvider.VoidChunkGenerator;

import java.io.File;
import java.util.HashMap;

import static org.apache.commons.io.FileUtils.deleteDirectory;

@Slf4j
public class worldManager {
    private final HashMap<String, World> worldMap = new HashMap<>();

    public String createWorld(Player worldOwner) {
        String sessionId = CRC32CHash.CRC32C(String.valueOf(worldOwner.getUniqueId()));
        String worldPath = "MatchWorlds/" + sessionId;

        WorldCreator creator = new WorldCreator(worldPath);

        creator.environment(World.Environment.NORMAL);
        creator.type(WorldType.FLAT);
        creator.generator(new VoidChunkGenerator());

        World world = creator.createWorld();
        worldMap.put(sessionId, world);
        return sessionId;
    }

    public void deleteWorld(String sessionId) {
        if (worldMap.containsKey(sessionId)) {
            World world = worldMap.get(sessionId);

            File folder = world.getWorldFolder();
            Bukkit.unloadWorld(world, false);

            try {
                deleteDirectory(folder);
            } catch (Exception e) {
                ClashArenaLogger.error(e.getMessage());
            }

            worldMap.remove(sessionId);
        }
    }

    public World getWorld(String sessionId) {
        return worldMap.get(sessionId);
    }

}
