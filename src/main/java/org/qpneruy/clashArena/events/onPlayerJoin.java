package org.qpneruy.clashArena.events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.qpneruy.clashArena.ClashArena;
import org.qpneruy.clashArena.data.ArenaPlayerManager;

public class onPlayerJoin implements Listener {
    private final ArenaPlayerManager ArenaPlayerManager;

    public onPlayerJoin(ArenaPlayerManager arenaPlayerManager) {
        this.ArenaPlayerManager = arenaPlayerManager;
        Bukkit.getPluginManager().registerEvents(this, ClashArena.instance);
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.ArenaPlayerManager.computeArenaPlayer(event.getPlayer().getUniqueId());
    }
}
