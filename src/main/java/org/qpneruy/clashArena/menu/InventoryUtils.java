package org.qpneruy.clashArena.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InventoryUtils {
    private static final Map<Material, ItemStack> BASIC_ITEMS_CACHE = new HashMap<>();
    private static final Map<ItemCacheKey, ItemStack> NAMED_ITEMS_CACHE = new HashMap<>();
    private static final Map<ItemCacheKey, ItemStack> CUSTOM_ITEMS_CACHE = new HashMap<>();

    /**
     * Sets pane items at specified positions in an inventory
     */
    public static void setPane(Inventory gui, int[] positions, Material material) {
        ItemStack item = getBasicItem(material);
        for (int position : positions) {
            gui.setItem(position, item);
        }
    }

    /**
     * Creates an item with custom name and lore
     */
    public static ItemStack createItem(Material material, String name, List<String> lore) {
        if (lore == null || lore.isEmpty()) {
            return createItem(material, name);
        }

        ItemCacheKey key = new ItemCacheKey(material, name, lore);
        return CUSTOM_ITEMS_CACHE.computeIfAbsent(key, k -> {
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();

            if (name != null) {
                meta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', name)));
            }

            List<Component> loreComponents = new ArrayList<>();
            for (String line : lore) {
                if (line != null) {
                    loreComponents.add(Component.text(ChatColor.translateAlternateColorCodes('&', line)));
                }
            }
            meta.lore(loreComponents);

            item.setItemMeta(meta);
            return item.clone();
        });
    }

    /**
     * Creates an item with only a custom name
     */
    public static ItemStack createItem(Material material, String name) {
        ItemCacheKey key = new ItemCacheKey(material, name, null);
        return NAMED_ITEMS_CACHE.computeIfAbsent(key, k -> {
            ItemStack item = new ItemStack(material);
            if (name != null) {
                ItemMeta meta = item.getItemMeta();
                meta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', name)));
                item.setItemMeta(meta);
            }
            return item.clone();
        });
    }

    /**
     * Gets a basic item with no customization
     */
    private static ItemStack getBasicItem(Material material) {
        return BASIC_ITEMS_CACHE.computeIfAbsent(material, key -> {
            ItemStack newItem = new ItemStack(key);
            ItemMeta meta = newItem.getItemMeta();
            meta.displayName(Component.text(""));
            newItem.setItemMeta(meta);
            return newItem.clone();
        });
    }

    /**
     * Key class for caching items
     */
    private static class ItemCacheKey {
        private final Material material;
        private final String name;
        private final List<String> lore;

        public ItemCacheKey(Material material, String name, List<String> lore) {
            this.material = material;
            this.name = name;
            this.lore = lore;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ItemCacheKey that = (ItemCacheKey) o;
            return material == that.material &&
                    Objects.equals(name, that.name) &&
                    Objects.equals(lore, that.lore);
        }

        @Override
        public int hashCode() {
            return Objects.hash(material, name, lore);
        }
    }
}