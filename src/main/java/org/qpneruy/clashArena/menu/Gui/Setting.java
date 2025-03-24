package org.qpneruy.clashArena.menu.Gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.qpneruy.clashArena.ClashArena;
import org.qpneruy.clashArena.Party.Mode;
import org.qpneruy.clashArena.menu.Gui.leader.AbstractPlayerMenu;
import org.qpneruy.clashArena.menu.core.AbstractMenu;
import org.qpneruy.clashArena.menu.enums.Menu;
import org.qpneruy.clashArena.menu.core.MenuButton;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Material.*;
import static org.qpneruy.clashArena.menu.InventoryUtils.createItem;
import static org.qpneruy.clashArena.menu.InventoryUtils.setPane;

public class Setting extends AbstractMenu {
    private final Map<Integer, ItemStack> modeItemsState = new HashMap<>();
    private int activeMode;
    private final AbstractPlayerMenu playerMenu;

    public Setting(Player menuOwner, AbstractPlayerMenu playerMenu) {
        super(Menu.SETTING, menuOwner, 27, "Cài Đặt");
        this.playerMenu = playerMenu;
        this.activeMode = playerMenu.getPARTIES_SIZE().ordinal() + 1;

        for (int i = 1; i <= 4; i++) {
            Material material = (i == activeMode) ? ENCHANTED_BOOK : BOOK;
            modeItemsState.put(i, createItem(material, String.valueOf(i)));
        }

        initButtons();
    }

    /**
     * Decorates the menu with buttons and other elements.
     */
    @Override
    protected void decorate() {
        Inventory gui = getInventory();

        setPane(gui, new int[]{2, 6, 20, 24}, CHAIN);
        setPane(gui, new int[]{0, 8, 10, 16, 18, 26}, OAK_SIGN);
        setPane(gui, new int[]{1, 7, 9, 17, 19, 25}, IRON_BARS);
        setPane(gui, new int[]{3, 4, 5, 21, 22, 23}, GRINDSTONE);
    }

    /**
     * Changes the selected mode state and updates UI
     */
    public ItemStack changeMode(int newMode) {
        if (newMode < 1 || newMode > 4 || newMode == activeMode) {
            return modeItemsState.get(newMode);
        }

        modeItemsState.put(activeMode, createItem(BOOK, String.valueOf(activeMode)));
        updateIcon(getModeSlot(activeMode), modeItemsState.get(activeMode));

        activeMode = newMode;
        modeItemsState.put(activeMode, createItem(ENCHANTED_BOOK, String.valueOf(activeMode)));

        playerMenu.setPARTIES_SIZE(Mode.values()[activeMode - 1]);

        return modeItemsState.get(activeMode);
    }

    /**
     * Gets the inventory slot for a given mode
     */
    private int getModeSlot(int mode) {
        return mode < 3 ? 10 + mode : 11 + mode;
    }
    private void initButtons() {
        buttons.put(11, new MenuButton.Builder()
                .icon(modeItemsState.get(1))
                .onClick(event -> updateIcon(11, changeMode(1)))
                .build());

        buttons.put(12, new MenuButton.Builder()
                .icon(modeItemsState.get(2))
                .onClick(event -> updateIcon(12, changeMode(2)))
                .build());

        buttons.put(14, new MenuButton.Builder()
                .icon(modeItemsState.get(3))
                .onClick(event -> updateIcon(14, changeMode(3)))
                .build());

        buttons.put(15, new MenuButton.Builder()
                .icon(modeItemsState.get(4))
                .onClick(event -> updateIcon(15, changeMode(4)))
                .build());
        super.buttonMap();
    }
    @Override
    protected void buttonMap() {
        buttons.put(13, new MenuButton.Builder()
                .icon(createItem(END_CRYSTAL, "§b§lQuay Lại"))
                .onClick(event -> {
                    ClashArena.instance.getMenuManager().openMenu((Player) event.getWhoClicked(), Menu.LEADER);
                })
                .build());

        super.buttonMap();
    }
}