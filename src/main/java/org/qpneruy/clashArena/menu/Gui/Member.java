package org.qpneruy.clashArena.menu.Gui;

import org.bukkit.Material;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import org.qpneruy.clashArena.menu.core.AbstractMenu;
import org.qpneruy.clashArena.menu.enums.Menu;
import org.qpneruy.clashArena.menu.core.MenuButton;
import org.qpneruy.clashArena.menu.manager.AbstractPlayerMenu;

import static org.bukkit.Material.END_CRYSTAL;

import static org.qpneruy.clashArena.menu.InventoryUtils.createItem;
import static org.qpneruy.clashArena.menu.InventoryUtils.setPane;


public class Member extends AbstractMenu {
    private AbstractPlayerMenu playerManager;

    public Member(Player menuOwner) {
        super(Menu.MEMBER, menuOwner,3 * 9, "§6§lThành Viên");
    }

    @Override
    public void decorate() {
        Inventory gui = this.getInventory();
        int[] IRONBARS = {0, 8, 9, 17, 18, 26};
        setPane(gui, IRONBARS, Material.IRON_BARS);

        int[] GRINDSTONES = {21, 22, 23};
        setPane(gui, GRINDSTONES, Material.GRINDSTONE);

        int[] CHAINS = {10, 16, 19, 25};
        setPane(gui, CHAINS, Material.CHAIN);

        int[] OAKSINGS = {1, 4, 7};
        setPane(gui, OAKSINGS, Material.OAK_SIGN);

        gui.setItem(2, createItem(Material.BREWING_STAND, "§6Chủ Phòng"));
    }

    public void setMemberManager(AbstractPlayerMenu playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    protected void buttonMap() {
        buttons.put(13, new MenuButton.Builder()
                .icon(createItem(END_CRYSTAL,
                        "§e§lMời"))
                .onClick(event -> {}).build());
        buttons.put(20, new MenuButton.Builder()
                .icon(createItem(Material.RED_STAINED_GLASS_PANE, "§c§lThoát Nhóm"))
                .onClick(event -> {
                    playerManager.removePlayer(event.getWhoClicked().getUniqueId());
                }).build());
        buttons.put(24, new MenuButton.Builder()
               .icon(createItem(Material.GREEN_STAINED_GLASS_PANE, "§a§oSẵn Sàng"))
                       .onClick(event -> {
                            playerManager.updatePlayerStatus(event.getWhoClicked().getUniqueId());
                       }).build());

        super.buttonMap();
    }
}
