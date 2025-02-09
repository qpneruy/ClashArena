package org.qpneruy.clashArena.menu.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import static org.qpneruy.clashArena.ClashArena.menuRegister;

/**
 * Listener for handling menu-related events.
 */
public class MenuEventListener implements Listener {


    @EventHandler()
    public void onClick(InventoryClickEvent event) {
        menuRegister.getHandler(event.getInventory())
                .ifPresent(handler -> handler.onClick(event));
    }

    @EventHandler()
    public void onOpen(InventoryOpenEvent event) {
        menuRegister.getHandler(event.getInventory())
                .ifPresent(handler -> handler.onOpen(event));
    }

    @EventHandler()
    public void onClose(InventoryCloseEvent event) {
        menuRegister.getHandler(event.getInventory())
                .ifPresent(handler -> handler.onClose(event));
    }
}