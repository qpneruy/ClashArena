package org.qpneruy.clashArena.events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.qpneruy.clashArena.ClashArena;
import org.qpneruy.clashArena.data.ArenaPlayerManager;
import org.qpneruy.clashArena.data.ArenaPlayerRepo;
import org.qpneruy.clashArena.menu.core.AbstractMenu;
import org.qpneruy.clashArena.menu.enums.Menu;
import org.qpneruy.clashArena.model.ArenaPlayer;

import java.util.Map;

public class onPlayerQuit implements Listener {
    ArenaPlayerManager ArenaPlayerManager;
    ArenaPlayerRepo ArenaPlayerStore;

    public onPlayerQuit(ArenaPlayerManager arenaPlayerManager, ArenaPlayerRepo arenaPlayerStore) {
        this.ArenaPlayerManager = arenaPlayerManager;
        this.ArenaPlayerStore = arenaPlayerStore;
        Bukkit.getPluginManager().registerEvents(this, ClashArena.instance);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        ArenaPlayer player = ArenaPlayerManager.computeArenaPlayer(event.getPlayer().getUniqueId());
        ArenaPlayerStore.save(player);
        ArenaPlayerManager.removePlayer(event.getPlayer().getUniqueId());
        final Map<Menu, AbstractMenu> tmp = ClashArena.instance.getMenuManager().getAllMenusForPlayer(event.getPlayer().getUniqueId());
        tmp.forEach((menu, abstractMenu) -> {
            abstractMenu.dispose();
        });
    }
}
