package org.qpneruy.clashArena.menu.core;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * Represents a button in a menu with an icon and a click handler.
 */
public class MenuButton {
    @Getter @Setter
    private ItemStack icon;
    private final Consumer<InventoryClickEvent> Funtion;

    private MenuButton(ItemStack icon, Consumer<InventoryClickEvent> Funtion) {
        this.icon = icon;
        this.Funtion = Funtion;
    }

    /**
     * Handles the click event for this button.
     *
     * @param event the inventory click event
     */
    public void onClick(InventoryClickEvent event) {
        if (Funtion != null) Funtion.accept(event);
    }

    /**
     * Builder for creating MenuButton instances.
     */
    public static class Builder {
        private ItemStack icon;
        private Consumer<InventoryClickEvent> clickHandler;

        /**
         * Sets the icon for the button.
         *
         * @param icon the icon to set
         * @return this builder instance
         */
        public Builder icon(ItemStack icon) {
            this.icon = icon;
            return this;
        }

        /**
         * Sets the click handler for the button.
         *
         * @param clickHandler the click handler to set
         * @return this builder instance
         */
        public Builder onClick(Consumer<InventoryClickEvent> clickHandler) {
            this.clickHandler = clickHandler;
            return this;
        }

        /**
         * Builds the MenuButton instance.
         *
         * @return the created MenuButton instance
         */
        public MenuButton build() {
            return new MenuButton(icon, clickHandler);
        }
    }
}