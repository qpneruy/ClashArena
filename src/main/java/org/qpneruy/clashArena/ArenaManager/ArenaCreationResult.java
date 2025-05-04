package org.qpneruy.clashArena.ArenaManager;

import org.bukkit.Location;

public record ArenaCreationResult(String arenaId, Location spawnLocation, boolean success, String errorMessage) {
    public static ArenaCreationResult success(String arenaId, Location spawn) {
        return new ArenaCreationResult(arenaId, spawn, true, null);
    }

    public static ArenaCreationResult failure(String arenaId, String message) {
        return new ArenaCreationResult(arenaId, null, false, message);
    }

    public static ArenaCreationResult creationFailure(String message) {
        return new ArenaCreationResult(null, null, false, message);
    }
}