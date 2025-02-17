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

public class Request extends AbstractMenu {


    public Request(Player menuOwner) {
        super(Menu.REQUEST, menuOwner,27,"Request");
    }

    /**
     * Decorates the menu with buttons and other elements.
     */
    @Override
    protected void decorate() {
        Inventory gui = this.getInventory();

        int[] CHAINS = {0, 8, 9, 17, 18, 26};
        setPane(gui, CHAINS, Material.CHAIN);

        setPane(gui, new int[]{19, 25}, Material.IRON_BARS);
        setPane(gui, new int[]{20, 24}, Material.OAK_SIGN);
        setPane(gui, new int[]{21, 23}, Material.GRINDSTONE);

        buttonMap();
    }

    @Override
    protected void buttonMap() {
        buttons.put(22, new MenuButton.Builder()
                .icon(createItem(Material.END_CRYSTAL,"Quay Lại"))
                .onClick(event -> {
                    ClashArena.instance.getMenuManager().openMenu((Player) event.getWhoClicked(), Menu.LEADER);
                }).build());
        super.buttonMap();
    }
}
