package org.qpneruy.clashArena.menu.Gui.leader;

import com.alessiodp.parties.api.interfaces.Party;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.qpneruy.clashArena.menu.core.MenuButton;
import org.qpneruy.clashArena.model.ArenaPlayer;
import org.qpneruy.clashArena.skin.ElybySkin;
import org.qpneruy.clashArena.utils.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Material.BREWING_STAND;
import static org.bukkit.Material.RED_WOOL;
import static org.qpneruy.clashArena.menu.InventoryUtils.createItem;

public class AbstractPlayerMenu {
    private Map<UUID, ArenaPlayer> Players = new HashMap<>();

    private final Inventory inventory;
    protected Map<Integer, MenuButton> buttons;
    private Party party;

    private int[] slots = {11, 12, 14, 15};
    private final Map<String, Integer> PlayerSlots = new HashMap<>();
    private Pair<ItemStack, ItemStack> emptySlot = new Pair<>(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

    public AbstractPlayerMenu(Inventory PartyInven, Map<Integer, MenuButton> buttons, Party party, String LeaderName) {
        this.inventory = PartyInven;
        this.buttons = buttons;
        this.party = party;

        for (Integer slot : slots) this.PlayerSlots.put(null, slot);

        PlayerHeadCreator(LeaderName, "♗ ", slots[0]);
        this.inventory.setItem(slots[0], createItem(BREWING_STAND, "Chủ Phòng"));

    }

    private void importPlayer(Party party) {
        party.getMembers().forEach((UUID uuid) -> {


        });
    }
    public void PlayerHeadCreator(String playerName, String prefix, int slot) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        ElybySkin.getPlayerHead(head, playerName, prefix)
                .thenAccept(v -> {
                    this.inventory.setItem(slot, head);
                    this.inventory.setItem(slot-9, createItem(RED_WOOL, "§c§oChưa Sẵn Sàng §l✗"));
                    this.PlayerSlots.put(playerName, slot);
                });
        buttons.put(slot, new MenuButton.Builder()
                .icon(head)
                .onClick(Event -> {
                    //TODO: kick, or profile viewer
                    System.out.println("Da kich hoat su kien click");
                }).build());
    }

    public void PlayerHeadRemover(String playerName) {
        int slot = PlayerSlots.get(playerName);
        this.inventory.setItem(slot, emptySlot.getFirst());
        this.inventory.setItem(slot-9, emptySlot.getSecond());
        this.PlayerSlots.put(null, slot);
    }

    public void PlayerChangeStatus(String playerName, boolean status) {
        int slot = PlayerSlots.get(playerName);
        this.inventory.setItem(slot - 9, createItem(status ? Material.LIME_WOOL : Material.RED_WOOL, status ? "§a§oSẵn Sàng §l✓" : "§c§oChưa Sẵn Sàng §l✗"));
    }


    //ItemStack Lore
    public void PlayerProfile() {

    }
}
