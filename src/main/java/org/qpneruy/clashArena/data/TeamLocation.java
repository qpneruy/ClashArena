package org.qpneruy.clashArena.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.qpneruy.clashArena.model.ArenaPlayer;

import java.util.HashMap;
import java.util.Objects;

public class TeamLocation {
    private HashMap<ArenaPlayer, Location> teamLocations = new HashMap<>();

    public void addPlayerToTeam(ArenaPlayer player, Location location) {
        teamLocations.put(player, location);
    }

    public void removePlayerFromTeam(ArenaPlayer player) {
        teamLocations.remove(player);
    }

    public void teleportTeamToLocation() {
        for (ArenaPlayer player : teamLocations.keySet()) {
            Location location = teamLocations.get(player);
            Objects.requireNonNull(Bukkit.getPlayer(player.getUniqueId())).teleport(location);
        }
    }

    public void Dispose() {
        teamLocations.clear();
        teamLocations = null;
    }
}
