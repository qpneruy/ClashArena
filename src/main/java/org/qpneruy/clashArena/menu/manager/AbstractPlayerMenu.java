package org.qpneruy.clashArena.menu.manager;

import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.qpneruy.clashArena.ClashArena;
import org.qpneruy.clashArena.Party.Mode;
import org.qpneruy.clashArena.menu.Gui.Member;
import org.qpneruy.clashArena.menu.core.MenuButton;
import org.qpneruy.clashArena.menu.enums.Menu;
import org.qpneruy.clashArena.model.ArenaPlayer;
import org.qpneruy.clashArena.model.ReadyStatus;
import org.qpneruy.clashArena.skin.ElybySkin;
import org.qpneruy.clashArena.utils.collections.Pair;

import java.util.*;

import static org.bukkit.Material.*;
import static org.qpneruy.clashArena.menu.InventoryUtils.createItem;

/**
 * Manages player slots, status and display in party-related menus
 */
public class AbstractPlayerMenu {

    // Constants
    private static final int LEADER_SLOT = 11;
    private static final String READY_TEXT = "§a§oSẵn Sàng §l✓";
    private static final String NOT_READY_TEXT = "§c§oChưa Sẵn Sàng §l✗";
    private static final String LEADER_LABEL = "Chủ Phòng";
    private static final String LEADER_PREFIX = "♗ ";
    private static final String MEMBER_PREFIX = "+ ";

    // UI Components
    private final Member memberGUI;
    private final Inventory leaderUI;
    private final Inventory memberUI;
    protected Map<Integer, MenuButton> buttons;
    private final Pair<ItemStack, ItemStack> emptySlotItems = new Pair<>(
            new ItemStack(GRAY_STAINED_GLASS_PANE),
            new ItemStack(BLACK_STAINED_GLASS_PANE));

    // Party data
    @Getter private final Party party;
    @Getter private Mode partiesSize = Mode.DUO;

    // Player tracking
    private final TreeSet<Integer> availableSeats = new TreeSet<>();
    private final BiMap<UUID, Integer> playerSlots = HashBiMap.create();
    private final Map<UUID, ArenaPlayer> partyPlayers = new HashMap<>();

    @Getter
    private boolean isFull = false;

    public void setPartiesSize(Mode mode) {
        this.partiesSize = mode;
        this.availableSeats.clear();
        initializeAvailableSeats();
        updatePartyFullStatus();
    }

    /**
     * Updates the isFull flag based on current party state
     */
    private void updatePartyFullStatus() {
        this.isFull = availableSeats.isEmpty();
    }

    /**
     * Creates a new party menu manager
     *
     * @param leaderInv The leaderUI to manage
     * @param memberGUI The memberGUI to manage
     * @param menuButtons Button map for the inventory
     * @param party The party associated with this menu
     * @param leaderName The name of the party leader
     */
    public AbstractPlayerMenu(Inventory leaderInv, Member memberGUI, Map<Integer, MenuButton> menuButtons,
                              Party party, String leaderName) {
        this.leaderUI = leaderInv;
        this.memberGUI = memberGUI;
        this.memberGUI.setMemberManager(this);
        this.memberUI = memberGUI.getInventory();
        this.buttons = menuButtons;
        this.party = party;

        initializeAvailableSeats();
        setupLeaderSlot(leaderName);
        updatePartyFullStatus();
    }

    /**
     * Initialize available player slots based on party size
     */
    private void initializeAvailableSeats() {
        for (int i = 0; i <= partiesSize.ordinal(); i++) {
            int slot = (i < 2 ? i : i + 2) + LEADER_SLOT;
            availableSeats.add(slot);
        }

        // Remove leader slot from available seats
        availableSeats.pollFirst();
    }

    /**
     * Setup the leader's slot with their head and label
     */
    private void setupLeaderSlot(String leaderName) {
        createPlayerHead(leaderName, LEADER_PREFIX, LEADER_SLOT);
        leaderUI.setItem(LEADER_SLOT - 9, createItem(BREWING_STAND, LEADER_LABEL));
        memberUI.setItem(LEADER_SLOT - 9, createItem(BREWING_STAND, LEADER_LABEL));

        UUID leaderUUID = party.getLeader();
        partyPlayers.put(leaderUUID, ClashArena.instance.getArenaPlayerManager()
                .computeArenaPlayer(leaderUUID));
    }

    /**
     * Adds a player to the party and assigns them a slot
     *
     * @param playerUUID UUID of the player to add
     * @return true if player was added successfully, false otherwise
     */
    public boolean addPlayer(UUID playerUUID) {
        if (availableSeats.isEmpty()) {
            updatePartyFullStatus();
            return false;
        }

        Integer seat = availableSeats.pollFirst();
        if (seat == null) return false;

        PartyPlayer partyPlayer = ClashArena.parties.getPartyPlayer(playerUUID);
        if (partyPlayer == null) return false;

        ArenaPlayer player = ClashArena.instance.getArenaPlayerManager().computeArenaPlayer(playerUUID);

        party.addMember(partyPlayer);
        playerSlots.put(playerUUID, seat);
        partyPlayers.put(playerUUID, player);

        createPlayerHead(player.getName(), MEMBER_PREFIX, seat);
        leaderUI.setItem(seat - 9, createItem(RED_WOOL, NOT_READY_TEXT));
        memberUI.setItem(seat - 9, createItem(RED_WOOL, NOT_READY_TEXT));

        ClashArena.instance.getMenuManager().openMenu(Bukkit.getPlayer(playerUUID), Menu.MEMBER, memberGUI);
        updatePartyFullStatus();
        return true;
    }

    /**
     * Removes a player from the party
     *
     * @param playerUUID UUID of the player to remove
     */
    public void removePlayer(UUID playerUUID) {
        Integer slot = playerSlots.get(playerUUID);
        if (slot == null) {
            return;
        }

        leaderUI.setItem(slot, emptySlotItems.getFirst());
        memberUI.setItem(slot, emptySlotItems.getFirst());
        leaderUI.setItem(slot - 9, emptySlotItems.getSecond());
        memberUI.setItem(slot - 9, emptySlotItems.getSecond());

        playerSlots.remove(playerUUID);
        partyPlayers.remove(playerUUID);
        availableSeats.add(slot);

        PartyPlayer partyPlayer = ClashArena.parties.getPartyPlayer(playerUUID);
        if (partyPlayer != null) {
            party.removeMember(partyPlayer);
        }
        ClashArena.instance.getMenuManager().closeMenu(Bukkit.getPlayer(playerUUID), Menu.MEMBER);
        updatePartyFullStatus();
    }

    /**
     * Updates a player's ready status in the UI
     *
     * @param playerUUID UUID of the player
     */
    public void updatePlayerStatus(UUID playerUUID) {
        Integer slot = playerSlots.get(playerUUID);
        ArenaPlayer player = partyPlayers.get(playerUUID);

        Material woolColor = player.Status == ReadyStatus.READY ? LIME_WOOL : RED_WOOL;
        String statusText = player.Status == ReadyStatus.READY ? READY_TEXT : NOT_READY_TEXT;

        leaderUI.setItem(slot - 9, createItem(woolColor, statusText));
        memberUI.setItem(slot - 9, createItem(woolColor, statusText));

        player.changeStatus();
    }

    /**
     * Creates a player head item and assigns it to a slot with click handling
     *
     * @param playerName Name of the player
     * @param prefix Prefix to display before the name
     * @param slot Inventory slot to place the head
     */
    private void createPlayerHead(String playerName, String prefix, int slot) {
        ItemStack head = new ItemStack(PLAYER_HEAD);
        leaderUI.setItem(slot, head);
        memberUI.setItem(slot, head);

        ElybySkin.getPlayerHead(head, playerName, prefix)
                .thenAccept(v -> {
                    leaderUI.setItem(slot, head);
                    memberUI.setItem(slot, head);
                });

        buttons.put(slot, new MenuButton.Builder()
                .icon(head)
                .onClick(event -> {
                    // TODO: Implement player interaction (kick, profile view)
                })
                .build());
    }

    public int getCurrentPartySize() {
        return this.partyPlayers.size();
    }
}