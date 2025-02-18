package org.qpneruy.clashArena.menu.Gui.mainMenu;

import com.alessiodp.parties.api.interfaces.Party;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.qpneruy.clashArena.ClashArena;
import org.qpneruy.clashArena.menu.Gui.Leader;
import org.qpneruy.clashArena.menu.core.AbstractMenu;
import org.qpneruy.clashArena.menu.enums.Menu;
import org.qpneruy.clashArena.menu.core.MenuButton;
import org.qpneruy.clashArena.menu.manager.Pagination;
import org.qpneruy.clashArena.utils.ClashArenaLogger;
import org.qpneruy.clashArena.utils.godQueue;

import java.util.*;

import static org.qpneruy.clashArena.menu.InventoryUtils.createItem;
import static org.qpneruy.clashArena.menu.InventoryUtils.setPane;

public class MainMenu extends AbstractMenu {

    godQueue<Party> parties = new godQueue<>();
    private final int MAX_PARTY = 28;

    private final List<Integer> availableSlots = Arrays.asList(1,2,3,4,5,6,7,10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34,46,48,50,52,54);



    public MainMenu(Player menuOwner) {
        super(Menu.MAIN, menuOwner,54, "Main Menu");

        // Register this menu to party manager,
        // so that the party manager can notify this menu when a party is created or removed.
        // Main Menu ONLY have 1 instance.
        ClashArena.instance.getPartyManager().registerListenerMenu(this);
        updateMenu();
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

    }

    //listener call from PartyManager
    public void addParty(Party party) {
        parties.add(party);
        updateMenu();
    }

    //listener call from PartyManager
    public void removeParty(Party party) {
        parties.remove(party);
        updateMenu();
    }

    private void updateMenu() {
       if (parties.getMethod() == UpdateMethod.NEW) {
           int current_idx = 0;
           for (Party party : parties) {
               if (current_idx >= availableSlots.size()) break;
               MenuButton button = RoomButtonCreator(parties.get(current_idx));
               buttons.put(availableSlots.get(current_idx), button);
               this.getInventory().setItem(availableSlots.get(current_idx), RoomButtonCreator(party).getIcon());
               current_idx++;
           }
           return;
       }
       if (parties.getMethod() == UpdateMethod.REMOVE) {
           int removedIndex = parties.getRemovedIndex();
           this.getInventory().clear(availableSlots.get(removedIndex));
           for (int i = removedIndex; i < parties.size(); i++) {
               this.getInventory().setItem(availableSlots.get(i), RoomButtonCreator(parties.get(i)).getIcon());
           }
           this.getInventory().clear(availableSlots.get(parties.size()));
       }
       this.buttonMap();
    }

    private MenuButton RoomButtonCreator(Party party) {
        return new MenuButton.Builder()
                .icon(createItem(Material.OAK_FENCE_GATE, party.getName(), Arrays.asList(
                        "§7§lLeader: §f" + Bukkit.getOfflinePlayer(Objects.requireNonNull(party.getLeader())).getName(),
                        "§7§lMembers: §f" + party.getMembers().size(),
                        "§7§lTag: §f" + party.getTag(),
                        "§7§lDescription: §f" + party.getDescription()
                )))
                .onClick(event -> {
                    if (party.getMembers().size() == 4) {
                        event.getWhoClicked().sendMessage("§c§lPhòng đã đầy.");
                    }
                    int pressIndex = availableSlots.indexOf(event.getSlot());
                    Party partyToJoin = parties.get(pressIndex);
                    UUID leaderOfParty = partyToJoin.getLeader();
                    Leader leaderMenu = (Leader) ClashArena.instance.getMenuManager().getSpecificMenuForPlayer(leaderOfParty, Menu.LEADER);
                    leaderMenu.test((Player) event.getWhoClicked());
                }).build();
    }

    @Override
    protected void buttonMap() {
        buttons.put(52, new MenuButton.Builder()
                .icon(createItem(Material.BLUE_BANNER, "§aTạo Phòng"))
                .onClick(event -> {
                    if (parties.size() == MAX_PARTY) {
                        event.getWhoClicked().sendMessage("§c§lĐã đầy, Không thể tạo phòng mới.");
                        return;
                    }

                    // "open menu" method include create a new one if the opening menu isn't exist
                    ClashArena.instance.getMenuManager().openMenu((Player) event.getWhoClicked(), Menu.LEADER);
                }).build());
        super.buttonMap();
    }
}
