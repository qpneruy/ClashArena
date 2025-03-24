package org.qpneruy.clashArena.menu.Gui.leader;

import com.alessiodp.parties.api.interfaces.Party;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.qpneruy.clashArena.ClashArena;
import org.qpneruy.clashArena.Party.Mode;
import org.qpneruy.clashArena.menu.core.MenuButton;
import org.qpneruy.clashArena.model.ArenaPlayer;
import org.qpneruy.clashArena.skin.ElybySkin;
import org.qpneruy.clashArena.utils.Pair;

import java.util.*;

import static org.bukkit.Material.BREWING_STAND;
import static org.bukkit.Material.RED_WOOL;
import static org.qpneruy.clashArena.menu.InventoryUtils.createItem;

public class AbstractPlayerMenu {
    protected Map<Integer, MenuButton> buttons;
    private Inventory inventory;
    @Getter private Party party;
    @Getter @Setter Mode PARTIES_SIZE = Mode.DOU;

    private final TreeSet<Integer> availableSeats = new TreeSet<>();
    private final int LEADER_SLOT = 11;

    private final BiMap<UUID, Integer> PlayerSlots = HashBiMap.create();
    private final Map<UUID, ArenaPlayer> PartyPlayers = new HashMap<>();
    private final Pair<ItemStack, ItemStack> emptySlot = new Pair<>(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

    public AbstractPlayerMenu(Inventory PartyInven, Map<Integer, MenuButton> buttons, Party party, String LeaderName) {
        this.inventory = PartyInven;
        this.buttons = buttons;
        this.party = party;

        for (int i = 0; i<=PARTIES_SIZE.ordinal(); i++) availableSeats.add((i < 2 ? i : i+2) + (LEADER_SLOT));
        
        availableSeats.pollFirst(); //Remove the first slot, because it's for the leader.
        PlayerHeadCreator(LeaderName, "♗ ", LEADER_SLOT);
        PartyPlayers.put(party.getLeader(), ClashArena.instance.getArenaPlayerManager().getArenaPlayer(party.getLeader()));
        this.inventory.setItem(LEADER_SLOT - 9, createItem(BREWING_STAND, "Chủ Phòng"));

    }


    public boolean addPlayer(UUID playerUUID) {
        if (availableSeats.isEmpty()) return false; //Layer 1: Phong thu :))

        ArenaPlayer player = ClashArena.instance.getArenaPlayerManager().getArenaPlayer(playerUUID);
        Integer seat = availableSeats.pollFirst();
        if (seat == null) return false; // Phong thu qua cung :)) ko he ho henh, khong mot so ho.
        //Bo dong nay ra IDE se bao NPE.
        party.addMember(Objects.requireNonNull(ClashArena.parties.getPartyPlayer(playerUUID)));
        PlayerSlots.put(playerUUID, seat);
        PartyPlayers.put(playerUUID, player);

        PlayerHeadCreator(player.getName(), "+ ", seat);
        return true;
    }

    public void PlayerHeadCreator(String playerName, String prefix, int slot) {
        System.out.println("Skin " + playerName);
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        this.inventory.setItem(slot, head);
        ElybySkin.getPlayerHead(head, playerName, prefix)
                .thenAccept(v -> {
                    this.inventory.setItem(slot, head);
                    if (slot != LEADER_SLOT) this.inventory.setItem(slot-9, createItem(RED_WOOL, "§c§oChưa Sẵn Sàng §l✗"));
                });
        buttons.put(slot, new MenuButton.Builder()
                .icon(head)
                .onClick(Event -> {
                    //TODO: kick, or profile viewer
                    System.out.println("Da kich hoat su kien click");
                }).build());
    }

    public void removePlayer(UUID playerUUID) {
        Integer slot = PlayerSlots.get(playerUUID);

        this.inventory.setItem(slot, emptySlot.getFirst());
        this.inventory.setItem(slot - 9, emptySlot.getSecond());

        PlayerSlots.remove(playerUUID);
        PartyPlayers.remove(playerUUID);

        availableSeats.add(slot);
        party.removeMember(Objects.requireNonNull(ClashArena.parties.getPartyPlayer(playerUUID)));
    }

    public void PlayerChangeStatus(UUID playerUUID, boolean status) {
        int slot = PlayerSlots.get(playerUUID);
        this.inventory.setItem(slot - 9, createItem(status ? Material.LIME_WOOL : Material.RED_WOOL, status ? "§a§oSẵn Sàng §l✓" : "§c§oChưa Sẵn Sàng §l✗"));
        this.PartyPlayers.get(playerUUID).changeStatus();
    }


    //ItemStack Lore
    public void PlayerProfile() {

    }
}
