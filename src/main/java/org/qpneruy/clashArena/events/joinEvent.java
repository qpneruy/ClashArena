package org.qpneruy.clashArena.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.qpneruy.clashArena.menu.core.AbstractMenu;
import org.qpneruy.clashArena.menu.enums.Menu;

import java.util.Map;

import static org.qpneruy.clashArena.ClashArena.menuManager;

public class joinEvent implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
         final Map<Menu, AbstractMenu> tmp = menuManager.getAllMenusForPlayer(event.getPlayer().getUniqueId());
         tmp.forEach((menu, abstractMenu) -> {
             abstractMenu.dispose();
         });
    }
}
