package org.qpneruy.clashArena.menu.Gui.Request;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.qpneruy.clashArena.ClashArena;
import org.qpneruy.clashArena.menu.manager.AbstractPlayerMenu;
import org.qpneruy.clashArena.menu.core.AbstractMenu;
import org.qpneruy.clashArena.menu.enums.Menu;
import org.qpneruy.clashArena.menu.core.MenuButton;
import org.qpneruy.clashArena.skin.ElybySkin;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static org.bukkit.Material.PLAYER_HEAD;
import static org.qpneruy.clashArena.menu.InventoryUtils.createItem;
import static org.qpneruy.clashArena.menu.InventoryUtils.setPane;

public class Request extends AbstractMenu {
    private final AbstractPlayerMenu playerManager;
    private final int[] slots = {1, 2, 3, 4, 5, 6, 7, 10, 11, 12, 13, 14, 15, 16};
    private final TreeSet<Integer> availableSlots = new TreeSet<>();
    private final Set<Player> Request = new HashSet<>();

    public Request(Player menuOwner, AbstractPlayerMenu playerManager) {
        super(Menu.REQUEST, menuOwner,27,"Request");
        this.playerManager = playerManager;
        for (int seat : slots) availableSlots.add(seat);
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

    }

    public void addRequest(Player player) {
        if (this.playerManager.isFull()) return;
        if (Request.contains(player)) return;
        ItemStack head = new ItemStack(PLAYER_HEAD);
        Integer slot = availableSlots.pollFirst();
        if (slot == null) return;
        ElybySkin.getPlayerHead(head, player.getName(), "")
                .thenAccept(v -> this.inventory.setItem(slot, head));
        buttons.put(slot, new MenuButton.Builder()
                .icon(head).onClick(event -> {
                    playerManager.addPlayer(player.getUniqueId());
                    buttons.remove(slot);
                    this.inventory.clear(slot);
                    availableSlots.add(slot);
                }).build());
        Request.add(player);
        super.buttonMap();
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
