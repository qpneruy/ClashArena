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

public class InventoryUtils {


    private static final Map<Material, ItemStack> CREATED_ITEMS = new HashMap<>();

    public static void setPane(Inventory gui, int[] positions, Material material) {
        ItemStack item = getItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(""));
        item.setItemMeta(meta);
        for (int position : positions) {
            gui.setItem(position, item);
        }
    }

    public static ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = getItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (name != null) {
            meta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', name)));
        }
        if (lore != null) {
            List<Component> loreComponents = new ArrayList<>();
            for (String line : lore) {
                if (line != null) {
                    loreComponents.add(Component.text(ChatColor.translateAlternateColorCodes('&', line)));
                }
            }
            meta.lore(loreComponents);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(Material material, String name) {
        return createItem(material, name, null);
    }

    private static ItemStack getItemStack(Material material) {
        return CREATED_ITEMS.computeIfAbsent(material, key -> {
            ItemStack newItem = new ItemStack(key);
            newItem.getItemMeta().displayName(Component.text(""));
            return newItem;
        });
    }
}
