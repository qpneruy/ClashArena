package org.qpneruy.clashArena.menu.events;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.qpneruy.clashArena.ClashArena;
import org.qpneruy.clashArena.menu.core.AbstractMenu;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Registry for managing active menus and their handlers.
 */
public class MenuRegistry {
    private final Map<Inventory, InventoryHandler> activeMenus;

    public MenuRegistry() {
        this.activeMenus = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(new MenuEventListener(), ClashArena.instance);
    }



    /**
     * Registers a menu with the registry.
     *
     * @param menu the menu to register
     */
    public void register(AbstractMenu menu) {
        if (!this.activeMenus.containsKey(menu.getInventory())) this.activeMenus.put(menu.getInventory(), menu);
    }

    /**
     * Unregisters a menu from the registry.
     *
     * @param menu the menu to unregister
     */
    public void unregister(AbstractMenu menu) {
        this.activeMenus.remove(menu.getInventory());
    }

    /**
     * Gets the handler associated with the given inventory.
     *
     * @param inventory the inventory to get the handler for
     * @return an Optional containing the handler if it exists, otherwise empty
     */
    public Optional<InventoryHandler> getHandler(Inventory inventory) {
        return Optional.ofNullable(this.activeMenus.get(inventory));
    }
}