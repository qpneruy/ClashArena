package org.qpneruy.clashArena.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.qpneruy.clashArena.ClashArena;
import org.qpneruy.clashArena.menu.core.AbstractMenu;
import org.qpneruy.clashArena.menu.enums.Menu;

import java.util.Map;

public class LeftEvent implements Listener {
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        final Map<Menu, AbstractMenu> tmp = ClashArena.instance.getMenuManager().getAllMenusForPlayer(event.getPlayer().getUniqueId());
        tmp.forEach((menu, abstractMenu) -> {
            abstractMenu.dispose();
        });
    }
}
