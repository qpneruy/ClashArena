package org.qpneruy.clashArena.menu.events;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

/**
 * Interface for handling inventory events.
 */
public interface InventoryHandler {

    /**
     * Handles inventory click events.
     *
     * @param event the inventory click event
     */
    void onClick(InventoryClickEvent event);

    /**
     * Handles inventory open events.
     *
     * @param event the inventory open event
     */
    void onOpen(InventoryOpenEvent event);

    /**
     * Handles inventory close events.
     *
     * @param event the inventory close event
     */
    void onClose(InventoryCloseEvent event);

}