//package org.qpneruy.clashArena.menu.Gui.mainMenu;
//
//import org.bukkit.Material;
//import org.bukkit.entity.Player;
//import org.bukkit.inventory.Inventory;
//import org.qpneruy.clashArena.menu.core.AbstractMenu;
//import org.qpneruy.clashArena.menu.enums.Menu;
//
//import static org.qpneruy.clashArena.menu.InventoryUtils.setPane;
//
//public class page extends AbstractMenu {
//
//    public page(Player menuOwner) {
//        super(Menu.PLAYER_MENU, menuOwner, 54, "Main Menu");
//    }
//
//    /**
//     * Decorates the menu with buttons and other elements.
//     */
//    @Override
//    protected void decorate() {
//        Inventory gui = this.getInventory();
//
//        int[] CHAINSLOT = {0, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53};
//        setPane(gui, CHAINSLOT, Material.CHAIN);
//
//        buttonMap();
//    }
//
//    @Override
//    protected void buttonMap() {
//        super.buttonMap();
//    }
//}
