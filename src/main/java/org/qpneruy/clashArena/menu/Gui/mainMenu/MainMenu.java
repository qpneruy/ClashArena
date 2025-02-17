package org.qpneruy.clashArena.menu.Gui.mainMenu;

import com.alessiodp.parties.api.interfaces.Party;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.qpneruy.clashArena.ClashArena;
import org.qpneruy.clashArena.menu.core.AbstractMenu;
import org.qpneruy.clashArena.menu.enums.Menu;
import org.qpneruy.clashArena.menu.core.MenuButton;
import org.qpneruy.clashArena.menu.manager.Pagination;
import org.qpneruy.clashArena.utils.godQueue;

import java.util.*;

import static org.qpneruy.clashArena.menu.InventoryUtils.createItem;
import static org.qpneruy.clashArena.menu.InventoryUtils.setPane;

public class MainMenu extends AbstractMenu {

    godQueue<Party> parties = new godQueue<>();
    private int MAX_PARTY = 28;

    private List<Integer> availableSlots = Arrays.asList(1,2,3,4,5,6,7,10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34,46,48,50,52,54);



    public MainMenu(Player menuOwner) {
        super(Menu.MAIN, menuOwner,54, "Main Menu");

        // Register this menu to party manager,
        // so that the party manager can notify this menu when a party is created or removed.
        // Main Menu ONLY have 1 instance.
        ClashArena.instance.getPartyManager().registerListenerMenu(this);
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


    public void addParty(Party party) {
        if (parties.size() == MAX_PARTY) return;
        parties.add(party);
        updateMenu();
    }

    public void removeParty(Party party) {
        parties.remove(party);
        updateMenu();
    }

    private void updateMenu() {
       if (parties.getMethod() == UpdateMethod.ALL) {
           update_inrange(0, 54);

       }
    }

    //TODO: Implement this method, complete update party into main menu
    private void update_inrange(int start, int end) {
        Inventory gui = this.getInventory();
        int i = 0;
        for (Party party : parties) {
            if (i >= start && i < end) {
                gui.setItem(availableSlots.get(i), createItem(Material.BLUE_BANNER, PlaceholderAPI.setPlaceholders(menuOwner, "§aParty: %party_name%")));
            }
            i++;
        }

    }

    @Override
    protected void buttonMap() {
        buttons.put(52, new MenuButton.Builder()
                .icon(createItem(Material.BLUE_BANNER, "§aTạo Phòng"))
                .onClick(event -> {
                    ClashArena.instance.getMenuManager().openMenu((Player) event.getWhoClicked(), Menu.LEADER);
                }).build());
        super.buttonMap();
    }
}
