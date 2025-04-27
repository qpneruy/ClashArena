package org.qpneruy.clashArena.menu.manager;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.qpneruy.clashArena.ClashArena;
import org.qpneruy.clashArena.menu.Gui.leader.Leader;
import org.qpneruy.clashArena.menu.Gui.Member;
import org.qpneruy.clashArena.menu.core.AbstractMenu;
import org.qpneruy.clashArena.menu.enums.Menu;
import org.qpneruy.clashArena.menu.events.MenuRegistry;
import org.qpneruy.clashArena.utils.ClashArenaLogger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class MenuManager {
    private final Map<UUID, Map<Menu, AbstractMenu>> ACTIVE_MENUS = new ConcurrentHashMap<>();
    @Getter
    private final MenuRegistry menuRegistry;

    public MenuManager() {
        this.menuRegistry = new MenuRegistry();
    }
    /**
     * Opens a menuType for the specified player. If the menuType does not already exist for the player,
     * it creates a new menuType, registers it, and opens it. If the menuType already exists, it simply opens it.
     *
     * @param player the player for whom the menuType is to be opened
     * @param menuType   the menuType to be opened
     */
    public void openMenu(Player player, Menu menuType) {
        Map<Menu, AbstractMenu> playerMenus = ACTIVE_MENUS.computeIfAbsent(player.getUniqueId(), key -> new HashMap<>());

        ClashArenaLogger.info("Opening Menu");


        if (menuType == Menu.UNDEFINED) {
            if (playerMenus.containsKey(Menu.LEADER)){
                player.openInventory(playerMenus.get(Menu.LEADER).getInventory());
                return;
            }
            if (playerMenus.containsKey(Menu.MEMBER)){
                player.openInventory(playerMenus.get(Menu.MEMBER).getInventory());
                return;
            }
            menuType = Menu.MAIN;
        }

        if (!playerMenus.containsKey(menuType)) {
            AbstractMenu newMenu = createMenu(player, menuType);
            if (newMenu == null) return;

            if (menuType != Menu.MAIN) ClashArenaLogger.info("created new menuType");

            player.openInventory(newMenu.getInventory());
            playerMenus.put(menuType, newMenu);
            menuRegistry.register(newMenu);
        }
        player.openInventory(playerMenus.get(menuType).getInventory());
    }

    public void openMenu(Player player, Menu menuType, AbstractMenu Menu) {
        Map<Menu, AbstractMenu> playerMenus = ACTIVE_MENUS.computeIfAbsent(player.getUniqueId(), key -> new HashMap<>());

        ClashArenaLogger.info("Opening Menu");

        if (!playerMenus.containsKey(menuType)) {
            player.openInventory(Menu.getInventory());
            playerMenus.put(menuType, Menu);
            menuRegistry.register(Menu);
        }
        player.openInventory(playerMenus.get(menuType).getInventory());
    }

    public void closeMenu(Player player, Menu menuType) {
        if (!this.ACTIVE_MENUS.containsKey(player.getUniqueId())) return;
        Map<Menu, AbstractMenu> playerMenus = this.ACTIVE_MENUS.get(player.getUniqueId());
        playerMenus.remove(menuType);
        player.closeInventory();
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
                menuRegistry.unregister(temp);
                temp.dispose();
            }

            playerMenus.put(menuKey, newMenu);
            menuRegistry.register(newMenu);
        }
    }

    /**
     * Disposes of a menu for the specified player. If the menu exists, it unregisters and removes it.
     * This method is called in disposeMenu method in AbstractMenu class
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

        menuRegistry.unregister(menuToDispose);
        playerMenus.remove(menu);
    }

    public void disposeAllMenu() {
        ACTIVE_MENUS.forEach((uuid, menu) -> {
            menu.forEach((menuType, abstractMenu) -> {
                abstractMenu.dispose();
            });
        });
        ACTIVE_MENUS.clear();
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
            case MAIN -> ClashArena.instance.getMainMenu();
            default -> null;
        };
    }
}