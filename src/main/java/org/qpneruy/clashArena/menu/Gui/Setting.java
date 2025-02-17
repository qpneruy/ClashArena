package org.qpneruy.clashArena.menu.Gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.qpneruy.clashArena.ClashArena;
import org.qpneruy.clashArena.menu.core.AbstractMenu;
import org.qpneruy.clashArena.menu.enums.Menu;
import org.qpneruy.clashArena.menu.core.MenuButton;

import static org.qpneruy.clashArena.menu.InventoryUtils.createItem;
import static org.qpneruy.clashArena.menu.InventoryUtils.setPane;

public class Setting extends AbstractMenu {

    public Setting(Player menuOwner) {
        super(Menu.SETTING, menuOwner,27, "Setting");
    }

    /**
     * Decorates the menu with buttons and other elements.
     */
    @Override
    protected void decorate() {
        Inventory gui = getInventory();
        int[] CHAINS = {2, 6, 20, 24};
        setPane(gui, CHAINS, Material.CHAIN);

        int[] OAKSIGNS = {0, 8, 10, 16, 18, 26};
        setPane(gui, OAKSIGNS, Material.OAK_SIGN);

        int[] IRONBARS = {1, 7, 9, 17, 19, 25};
        setPane(gui, IRONBARS, Material.IRON_BARS);

        int[] GRINDSTONES = {3, 4, 5, 21, 22, 23};
        setPane(gui, GRINDSTONES, Material.GRINDSTONE);

        buttonMap();
    }

    @Override
    protected void buttonMap() {
        buttons.put(13, new MenuButton.Builder()
                .icon(createItem(Material.END_CRYSTAL,"Quay Lại"))
                .onClick(event -> {
                    ClashArena.instance.getMenuManager().openMenu((Player) event.getWhoClicked(), Menu.LEADER);
                }).build());
        super.buttonMap();
    }
}
