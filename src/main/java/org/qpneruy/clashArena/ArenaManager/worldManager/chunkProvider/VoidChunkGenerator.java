package org.qpneruy.clashArena.ArenaManager.worldManager.chunkProvider;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class VoidChunkGenerator extends ChunkGenerator {

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return List.of();
    }

    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ,
                              @NotNull ChunkData chunkData) {
    }

    @Override
    public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ,
                                @NotNull ChunkData chunkData) {
    }

    @Override
    public void generateBedrock(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ,
                                @NotNull ChunkData chunkData) {
    }

    @Override
    public void generateCaves(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ,
                              @NotNull ChunkData chunkData) {
    }

    @Override
    @Nullable
    public BiomeProvider getDefaultBiomeProvider(@NotNull WorldInfo worldInfo) {
        return new VoidBiomeProvider();
    }

    @Override
    public boolean canSpawn(World world, int x, int z) {
        return true;
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 0, 100, 0);
    }
}