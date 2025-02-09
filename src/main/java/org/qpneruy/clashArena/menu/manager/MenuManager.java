package org.qpneruy.clashArena.menu.manager;

import org.bukkit.entity.Player;
import org.qpneruy.clashArena.menu.Gui.Leader;
import org.qpneruy.clashArena.menu.Gui.Member;
import org.qpneruy.clashArena.menu.Gui.Request;
import org.qpneruy.clashArena.menu.Gui.Setting;
import org.qpneruy.clashArena.menu.Gui.mainMenu.MainMenu;
import org.qpneruy.clashArena.menu.core.AbstractMenu;
import org.qpneruy.clashArena.menu.enums.Menu;
import org.qpneruy.clashArena.utils.ClashArenaLogger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.qpneruy.clashArena.ClashArena.menuRegister;

public class MenuManager {
    private final Map<UUID, Map<Menu, AbstractMenu>> ACTIVE_MENUS = new ConcurrentHashMap<>();

    /**
     * Opens a menu for the specified player. If the menu does not already exist for the player,
     * it creates a new menu, registers it, and opens it. If the menu already exists, it simply opens it.
     *
     * @param player the player for whom the menu is to be opened
     * @param menu   the menu to be opened
     */
    public void openMenu(Player player, Menu menu) {
        Map<Menu, AbstractMenu> playerMenus = ACTIVE_MENUS.computeIfAbsent(player.getUniqueId(), key -> new HashMap<>());

        ClashArenaLogger.info("Opening Menu");

        if (!playerMenus.containsKey(menu)) {
            AbstractMenu newMenu = createMenu(player, menu);
            if (newMenu == null) return;

            ClashArenaLogger.info("created new menu");

            player.openInventory(newMenu.getInventory());
            playerMenus.put(menu, newMenu);
            menuRegister.register(newMenu);
        }
        player.openInventory(playerMenus.get(menu).getInventory());
    }

    /**
     * Imports a collection of menus for a player, adding or updating them. If a menu with the same key
     * already exists for the player, it will be replaced with the imported one. Otherwise, the new menu
     * will be added to the player's active menus.
     *
     * @param player   The player to import the menus for.
     * @param subMenus A map of menus to import, where the key is the {@link Menu} enum and the value is the {@link AbstractMenu} instance.
     */
    public void importMenu(Player player, Map<Menu, AbstractMenu> subMenus) {
        if (subMenus == null || subMenus.isEmpty()) {return;}
        Map<Menu, AbstractMenu> playerMenus = ACTIVE_MENUS.computeIfAbsent(player.getUniqueId(), key -> new HashMap<>());
        for (Map.Entry<Menu, AbstractMenu> entry : subMenus.entrySet()) {

            Menu menuKey = entry.getKey();
            AbstractMenu newMenu = entry.getValue();

            if (playerMenus.containsKey(menuKey)) {
                AbstractMenu temp = playerMenus.get(menuKey);
                menuRegister.unregister(temp);
                temp.dispose();
            }

            playerMenus.put(menuKey, newMenu);
            menuRegister.register(newMenu);
        }
    }

    /**
     * Disposes of a menu for the specified player. If the menu exists, it unregisters and removes it.
     * this method is called in disposeMenu method in AbstractMenu class
     * NOT recommended to call this method in another place
     *
     * @param player the player for whom the menu is to be disposed
     * @param menu   the menu to be disposed
     */
    public void disposeMenu(Player player, Menu menu) {
        Map<Menu, AbstractMenu> playerMenus = ACTIVE_MENUS.get(player.getUniqueId());
        if (playerMenus == null) return;

        AbstractMenu menuToDispose = playerMenus.get(menu);
        if (menuToDispose == null) return;

        menuRegister.unregister(menuToDispose);
        playerMenus.remove(menu);
    }

    /**
     * Retrieves all active menus for a player.
     *
     * @param playerId The UUID of the player.
     * @return An unmodifiable map of the player's active menus, or an empty map if the player has no active menus.
     */
    public Map<Menu, AbstractMenu> getAllMenusForPlayer(UUID playerId) {
        Map<Menu, AbstractMenu> playerMenus = ACTIVE_MENUS.get(playerId);
        return (playerMenus != null) ? Collections.unmodifiableMap(playerMenus) : Collections.emptyMap();
    }

    /**
     * Retrieves a specific menu for a player.
     *
     * @param playerId The UUID of the player.
     * @param menu     The type of menu to retrieve.
     * @return The requested AbstractMenu instance, or null if the player does not have that menu active.
     */
    public AbstractMenu getSpecificMenuForPlayer(UUID playerId, Menu menu) {
        Map<Menu, AbstractMenu> playerMenus = ACTIVE_MENUS.get(playerId);
        return (playerMenus != null) ? playerMenus.get(menu) : null;
    }

    private AbstractMenu createMenu(Player owner, Menu menu) {
        return switch (menu) {
            case LEADER -> new Leader(owner);
            case MEMBER -> new Member(owner);
            case SETTING -> new Setting(owner);
            case REQUEST -> new Request(owner);
            case MAIN -> new MainMenu(owner);
            default -> null;
        };
    }
}