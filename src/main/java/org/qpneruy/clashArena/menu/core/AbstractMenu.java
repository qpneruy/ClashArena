package org.qpneruy.clashArena.menu.core;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.qpneruy.clashArena.ClashArena;
import org.qpneruy.clashArena.menu.enums.Menu;
import org.qpneruy.clashArena.menu.events.InventoryHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base class for creating custom menus.
 */
public abstract class AbstractMenu implements InventoryHandler {
    protected Player menuOwner;
    protected Menu MenuType;
    @Getter protected Inventory inventory;
    protected Map<Integer, MenuButton> buttons;

    /**
     * Constructs a new AbstractMenu and initializes it.
     */
    protected AbstractMenu(Menu menuType, Player menuOwner, int size, String title) {
        this.menuOwner = menuOwner; this.MenuType = menuType;
        this.inventory = Bukkit.createInventory(null, size, Component.text(title));
        this.buttons = new HashMap<>();
        initialize();
    }

    /**
     * Decorates the menu with buttons and other elements.
     */
    protected abstract void decorate();

    /**
     * Adds a button to the menu.
     */
    protected void buttonMap() {
        buttons.forEach((slot, button) -> inventory.setItem(slot, button.getIcon()));
    }

    /**
     * Initializes the menu by decorating it and registering it with the MenuRegistry.
     */
    private void initialize() {decorate(); buttonMap();}

    /**
     * Updates the button in the specified slot.
     *
     * @param slot the slot to update, referenced from the hashmap
     * @param newicon the new icon to set
     */
    protected void updateIcon(int slot, ItemStack newicon) {
        MenuButton button = buttons.get(slot); button.setIcon(newicon);
        inventory.setItem(slot, button.getIcon());
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        MenuButton button = buttons.get(event.getSlot());
        if (button != null) button.onClick(event);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
    }

    /**
     * Clears the menu and releases resources for garbage collection.
     * It also unregisters the menu from the MenuRegistry.
     */
    public void dispose() {
        if (inventory != null) {
            inventory.clear();
            inventory = null;
        }
        if (buttons != null) {
            buttons.clear();
            buttons = null;
        }
        ClashArena.instance.getMenuManager().disposeMenu(this.menuOwner, this.MenuType);

        this.MenuType = null;
        this.menuOwner = null;
    }
}