package org.qpneruy.clashArena.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.qpneruy.clashArena.ClashArena;
import org.qpneruy.clashArena.model.ArenaPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ArenaPlayerManager {
    private Map<UUID, ArenaPlayer> players = new HashMap<>();


    public ArenaPlayer getArenaPlayer(UUID uuid) {
        return players.computeIfAbsent(uuid, id -> {
            Optional<ArenaPlayer> existingPlayer = ClashArena.instance.getArenaPlayerStore().findById(id);
            if (existingPlayer.isPresent()) {
                return existingPlayer.get();
            }

            Player bukkitPlayer = Bukkit.getPlayer(id);
            if (bukkitPlayer == null) {
                throw new IllegalStateException("Player with UUID " + id + " not found online");
            }

            return new ArenaPlayer(bukkitPlayer);
        });
    }

}
