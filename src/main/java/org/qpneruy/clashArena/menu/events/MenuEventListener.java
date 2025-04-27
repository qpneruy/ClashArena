package org.qpneruy.clashArena.menu.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.qpneruy.clashArena.ClashArena;
import org.qpneruy.clashArena.menu.enums.Menu;

/**
 * Listener for handling menu-related events.
 */
public class MenuEventListener implements Listener {
    private final MenuRegistry menuRegistry;
    public MenuEventListener(MenuRegistry menuRegistry) {
        this.menuRegistry = menuRegistry;
        ClashArena.instance.getServer().getPluginManager().registerEvents(this, ClashArena.instance);
    }


    @EventHandler()
    public void onClick(InventoryClickEvent event) {
        menuRegistry.getHandler(event.getInventory())
                .ifPresent(handler -> handler.onClick(event));
    }

    @EventHandler()
    public void onOpen(InventoryOpenEvent event) {
        menuRegistry.getHandler(event.getInventory())
                .ifPresent(handler -> handler.onOpen(event));
    }

    @EventHandler()
    public void onClose(InventoryCloseEvent event) {
        menuRegistry.getHandler(event.getInventory())
                .ifPresent(handler -> handler.onClose(event));
    }
}