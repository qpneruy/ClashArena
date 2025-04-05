package org.qpneruy.clashArena;


import lombok.Getter;
import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.qpneruy.clashArena.Party.AlessioDPPartyAdapter;
import org.qpneruy.clashArena.Party.IPartyAdapter;
import org.qpneruy.clashArena.Party.PartyManager;
import org.qpneruy.clashArena.commands.ClashArenaCmd;
import org.qpneruy.clashArena.commands.ClashArenaCompleter;
import org.qpneruy.clashArena.data.ArenaPlayerManager;
import org.qpneruy.clashArena.data.ArenaPlayerRepository;
import org.qpneruy.clashArena.menu.Gui.mainMenu.MainMenu;
import org.qpneruy.clashArena.menu.manager.MenuManager;
import org.qpneruy.clashArena.menu.events.MenuRegistry;
import org.qpneruy.clashArena.model.ArenaPlayer;
import org.qpneruy.clashArena.utils.ClashArenaLogger;
import org.qpneruy.clashArena.utils.enums.ConsoleColor;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public final class ClashArena extends JavaPlugin implements Listener {
    public static ClashArena instance;
    public static IPartyAdapter parties;

    private MenuManager menuManager;
    private MenuRegistry menuRegister;
    private PartyManager partyManager;
    private MainMenu mainMenu;
    private ArenaPlayerManager ArenaPlayerManager;
    private ArenaPlayerRepository ArenaPlayerStore;

    @Override
    public void onEnable() {
        instance = this;
        ClashArenaLogger.info( "ClashArena has been enabled!");
        getServer().getPluginManager().registerEvents(this, this);

        registerCommands();
        intializeService();
        hook();

        // Main Menu does not have a menu owner.
        mainMenu = new MainMenu(null);
        partyManager.registerListenerMenu(mainMenu);
    }

    private void intializeService() {
        menuManager = new MenuManager();
        menuRegister = new MenuRegistry();
        partyManager = new PartyManager();
        ArenaPlayerStore = new ArenaPlayerRepository();
        ArenaPlayerManager = new ArenaPlayerManager();
    }

    private void registerCommands() {
        new ClashArenaCmd(this);
        new ClashArenaCompleter(this);
    }

    private void hook() {
        Map<String, Boolean> pluginsToCheck = new LinkedHashMap<>() {{
            put("PlaceholderAPI", false);
            put("ItemsAdder", false);
            put("MMOItems", false);
            put("MythicMobs", false);
            put("Parties", true);
            put("FastAsyncWorldEdit", true);
            put("WorldEdit", true);
        }};

        for (Map.Entry<String, Boolean> entry : pluginsToCheck.entrySet()) {
            String pluginName = entry.getKey();
            boolean isDepend = entry.getValue();

            Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
            if (plugin != null && plugin.isEnabled()) {
                ClashArenaLogger.info(pluginName + ": " + ConsoleColor.GREEN + "hooked!");

                if (isDepend) {
                    switch (pluginName) {
                        case "Parties" -> parties = new AlessioDPPartyAdapter(Parties.getApi());
                        case "FastAsyncWorldEdit", "WorldEdit" -> {}
                    }
                }
            } else {
                ClashArenaLogger.info(pluginName + ": " + ConsoleColor.RED + "not found!");
            }
        }
    }

    @Override
    public void onDisable() {
        this.ArenaPlayerStore.close();
    }

    private String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    private Class<?> getNMSClass(String name) throws ClassNotFoundException {
        String fullName = "net.minecraft.server." + getVersion() + "." + name.replace('.', '/');
        return Class.forName(fullName);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.ArenaPlayerManager.computeArenaPlayer(event.getPlayer().getUniqueId());
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        ArenaPlayer player = this.ArenaPlayerManager.computeArenaPlayer(event.getPlayer().getUniqueId());
        this.ArenaPlayerStore.save(player);
        this.ArenaPlayerManager.removePlayer(event.getPlayer().getUniqueId());
    }
}
