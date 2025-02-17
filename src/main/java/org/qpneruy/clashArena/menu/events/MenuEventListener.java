package org.qpneruy.clashArena.menu.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.qpneruy.clashArena.ClashArena;

/**
 * Listener for handling menu-related events.
 */
public class MenuEventListener implements Listener {


    @EventHandler()
    public void onClick(InventoryClickEvent event) {
        ClashArena.instance.getMenuRegister().getHandler(event.getInventory())
                .ifPresent(handler -> handler.onClick(event));
    }

    @EventHandler()
    public void onOpen(InventoryOpenEvent event) {
        ClashArena.instance.getMenuRegister().getHandler(event.getInventory())
                .ifPresent(handler -> handler.onOpen(event));
    }

    @EventHandler()
    public void onClose(InventoryCloseEvent event) {
        ClashArena.instance.getMenuRegister().getHandler(event.getInventory())
                .ifPresent(handler -> handler.onClose(event));
    }
}