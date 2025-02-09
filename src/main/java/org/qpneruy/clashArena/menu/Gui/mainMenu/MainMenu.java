package org.qpneruy.clashArena.menu.Gui.mainMenu;

import com.alessiodp.parties.api.interfaces.Party;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.qpneruy.clashArena.ClashArena;
import org.qpneruy.clashArena.menu.core.AbstractMenu;
import org.qpneruy.clashArena.menu.enums.Menu;
import org.qpneruy.clashArena.menu.core.MenuButton;
import org.qpneruy.clashArena.menu.manager.Pagination;

import java.util.List;

import static org.qpneruy.clashArena.ClashArena.menuManager;
import static org.qpneruy.clashArena.menu.InventoryUtils.createItem;
import static org.qpneruy.clashArena.menu.InventoryUtils.setPane;

public class MainMenu extends AbstractMenu {
    Pagination<page> pages = new Pagination<>();

    public MainMenu(Player menuOwner) {
        super(Menu.MAIN, menuOwner,54, "Main Menu");
    }

    /**
     * Decorates the menu with buttons and other elements.
     */
    @Override
    protected void decorate() {
        Inventory gui = this.getInventory();

        int[] CHAINSLOT = {0, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53};
        setPane(gui, CHAINSLOT, Material.CHAIN);

        int[] OAKSIGN = {37, 39, 41, 43};
        setPane(gui, OAKSIGN, Material.OAK_SIGN);

        int[] IRONBAR = {38, 40, 42};
        setPane(gui, IRONBAR, Material.IRON_BARS);

        int[] GRINDSTONES = {47, 49, 51};
        setPane(gui, GRINDSTONES, Material.GRINDSTONE);

        buttonMap();
    }

    private void insertRoom() {
        List<Party> onlineParties = ClashArena.parties.getOnlineParties();

    }

    private MenuButton roomButtonCreator() {
        return new MenuButton.Builder()
                .icon(createItem(Material.LANTERN, "§lSAU ->"))
                .onClick(event -> {
                }).build();
    }
    @Override
    protected void buttonMap() {
        buttons.put(26, new MenuButton.Builder()
                .icon(createItem(Material.LANTERN, "§lSAU ->"))
                .onClick(event -> {
                }).build());
        buttons.put(35, new MenuButton.Builder()
                .icon(createItem(Material.SOUL_LANTERN, "§lTRƯỚC <-"))
                .onClick(event -> {
                }).build());
        buttons.put(52, new MenuButton.Builder()
                .icon(createItem(Material.BLUE_BANNER, "§aTạo Phòng"))
                .onClick(event -> {
                    menuManager.openMenu((Player) event.getWhoClicked(), Menu.LEADER);
                }).build());
        super.buttonMap();
    }
}
