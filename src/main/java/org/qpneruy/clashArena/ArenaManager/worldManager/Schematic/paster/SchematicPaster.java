package org.qpneruy.clashArena.ArenaManager.worldManager.Schematic.paster;

import org.bukkit.Location;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public interface SchematicPaster {
    CompletableFuture<Void> paste(File file, Location location, boolean ignoreAirBlock);
}