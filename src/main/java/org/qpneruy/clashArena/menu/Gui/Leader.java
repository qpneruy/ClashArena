package org.qpneruy.clashArena.menu.Gui;

import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.qpneruy.clashArena.ClashArena;
import org.qpneruy.clashArena.menu.core.AbstractMenu;
import org.qpneruy.clashArena.menu.enums.Menu;
import org.qpneruy.clashArena.menu.core.MenuButton;
import org.qpneruy.clashArena.menu.enums.Visibility;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Material.*;
import static org.qpneruy.clashArena.ClashArena.menuManager;
import static org.qpneruy.clashArena.ClashArena.parties;
import static org.qpneruy.clashArena.menu.InventoryUtils.createItem;
import static org.qpneruy.clashArena.menu.InventoryUtils.setPane;

public class Leader extends AbstractMenu {

    private Party party;
    protected Visibility visibility = Visibility.PRIVATE;
    protected Map<Menu, AbstractMenu> SubMenus = new HashMap<>();

    public Leader(Player menuOwner) {
        super(Menu.LEADER, menuOwner,27,"Leader");
        SubMenus.put(Menu.SETTING, new Setting(menuOwner));
        SubMenus.put(Menu.REQUEST, new Request(menuOwner));
        ClashArena.parties.createParty(menuOwner.getName(), parties.getPartyPlayer(menuOwner.getUniqueId()));
        //Import Submenus to the menuManager for easy access and reduce memory leak issue
        menuManager.importMenu(menuOwner, SubMenus);
        this.menuOwner = menuOwner;
        partyCreatorHelper();
    }

    /**
     * CreateParty with case check, is player already have party or not\
     */
    private void partyCreatorHelper() {
        final UUID ownerId = menuOwner.getUniqueId();
        party = parties.getParty(ownerId);

        if (party == null) {
            final PartyPlayer partyPlayer = parties.getPartyPlayer(ownerId);
            parties.createParty(menuOwner.getName(), partyPlayer);
            party = parties.getParty(ownerId);
        }
    }

    /**
     * Decorates the menu with buttons and other elements.
     */
    @Override
    protected void decorate() {
        Inventory gui = getInventory();
        int[] IRONBARS = {0, 8, 9, 17, 18, 26};
        setPane(gui, IRONBARS, IRON_BARS);

        int[] CHAINS = {10, 16, 19, 25};
        setPane(gui, CHAINS, CHAIN);

        int[] OAKSINGS = {1, 4, 7};
        setPane(gui, OAKSINGS, OAK_SIGN);
        buttonMap();
    }

    @Override
    protected void buttonMap() {
        buttons.put(20, new MenuButton.Builder()
                .icon(createItem(RED_STAINED_GLASS_PANE, "§c§lThoát Nhóm"))
                .onClick(event -> {
                    menuManager.openMenu((Player) event.getWhoClicked(), Menu.MAIN); dispose();

                    //Dispose all submenus
                    this.dispose(); SubMenus.forEach((menu, abstractMenu) -> abstractMenu.dispose());
                }).build());

        buttons.put(24, new MenuButton.Builder()
                .icon(createItem(GREEN_STAINED_GLASS_PANE, "§a§lGhép Trận"))
                .onClick(event -> {
                }).build());

        buttons.put(23, new MenuButton.Builder()
                .icon(visibilityDisplayIcon())
                .onClick(event -> {
                    visibility = visibility == Visibility.PRIVATE ? Visibility.PUBLIC : Visibility.PRIVATE;
                    updateIcon(23, visibilityDisplayIcon());
                }).build());

        buttons.put(22, new MenuButton.Builder()
                .icon(createItem(HOPPER, "§7§lCài Đặt"))
                .onClick(event -> {
                    menuManager.openMenu((Player) event.getWhoClicked(), Menu.SETTING);
                }).build());

        buttons.put(21, new MenuButton.Builder()
                .icon(createItem(FLOWER_BANNER_PATTERN,
                        "§b§lYêu Cầu Tham Gia"))
                .onClick(event ->{
                    menuManager.openMenu((Player) event.getWhoClicked(), Menu.REQUEST);
                }).build());

        buttons.put(13, new MenuButton.Builder()
                .icon(createItem(END_CRYSTAL,
                        "§e§lMời"))
                .onClick(event -> {}).build());
        super.buttonMap();
    }

    private ItemStack visibilityDisplayIcon() {
        return visibility == Visibility.PRIVATE ? createItem(IRON_DOOR, "§7§lCông Khai: §l§c✗") : createItem(OAK_DOOR, "§7§lCông Khai: §a✔");
    }

    @Override
    public void dispose() {
        super.dispose();
        SubMenus.clear();
        SubMenus = null;
        party = null;
    }
}
