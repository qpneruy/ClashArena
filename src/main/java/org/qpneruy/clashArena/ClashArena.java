package org.qpneruy.clashArena;


import lombok.Getter;
import com.alessiodp.parties.api.Parties;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.qpneruy.clashArena.Party.AlessioDPPartyAdapter;
import org.qpneruy.clashArena.Party.IPartyAdapter;
import org.qpneruy.clashArena.Party.PartyManager;
import org.qpneruy.clashArena.commands.ClashArenaCmd;
import org.qpneruy.clashArena.commands.ClashArenaCompleter;
import org.qpneruy.clashArena.data.ArenaPlayerManager;
import org.qpneruy.clashArena.data.ArenaPlayerRepository;
import org.qpneruy.clashArena.events.onPlayerJoin;
import org.qpneruy.clashArena.events.onPlayerQuit;
import org.qpneruy.clashArena.menu.Gui.mainMenu.MainMenu;
import org.qpneruy.clashArena.menu.events.MenuEventListener;
import org.qpneruy.clashArena.menu.manager.MenuManager;
import org.qpneruy.clashArena.utils.ClashArenaLogger;
import org.qpneruy.clashArena.utils.enums.ConsoleColor;
import org.qpneruy.clashArena.worldManager.Schematic.SchematicPasterManager;
import org.qpneruy.clashArena.worldManager.worldManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public final class ClashArena extends JavaPlugin {
    public static ClashArena instance;
    public static IPartyAdapter parties;

    private MenuManager menuManager;
    private PartyManager partyManager;
    private worldManager worldManager;
    private SchematicPasterManager schematicPasterManager;
    private ArenaPlayerManager ArenaPlayerManager;

    private MainMenu mainMenu;
    private ArenaPlayerRepository ArenaPlayerStore;

    @Override
    public void onEnable() {
        instance = this;
        ClashArenaLogger.info( "ClashArena has been enabled!");

        registerCommands();
        initializeConfig();
        initializeService();
        registerEvents();
        hook();

        // Main Menu does not have a menu owner.
        mainMenu = new MainMenu(null);
        partyManager.registerListenerMenu(mainMenu);
    }
    private void registerEvents() {
       new onPlayerJoin(ArenaPlayerManager);
       new onPlayerQuit(ArenaPlayerManager, ArenaPlayerStore);
       new MenuEventListener(menuManager.getMenuRegistry());
    }
    private void initializeService() {
        menuManager = new MenuManager();
        partyManager = new PartyManager();
        worldManager = new worldManager();
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
            put("WorldEdit", true);
        }};

        for (Map.Entry<String, Boolean> entry : pluginsToCheck.entrySet()) {
            String pluginName = entry.getKey();

            Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
            if (plugin != null && plugin.isEnabled()) {
                ClashArenaLogger.info(pluginName + ": " + ConsoleColor.GREEN + "hooked!");
                switch (pluginName) {
                    case "Parties" -> parties = new AlessioDPPartyAdapter(Parties.getApi());
                    case "WorldEdit" -> schematicPasterManager = new SchematicPasterManager();
                }
            } else {
                ClashArenaLogger.info(pluginName + ": " + ConsoleColor.RED + "not found!");
                if (pluginsToCheck.get(pluginName)) {
                    ClashArenaLogger.warn("Please install '" + pluginName + "' to use this plugin.");
                    Bukkit.getPluginManager().disablePlugin(this);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getServer().getOnlinePlayers().forEach(
                player -> ArenaPlayerStore.save(ArenaPlayerManager.computeArenaPlayer(player.getUniqueId())));
        this.ArenaPlayerStore.close();
    }

    private String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    private Class<?> getNMSClass(String name) throws ClassNotFoundException {
        String fullName = "net.minecraft.server." + getVersion() + "." + name.replace('.', '/');
        return Class.forName(fullName);
    }
    private void initializeConfig() {
        saveDefaultConfig();
        saveResourceToFolder("Default.yml", new File("plugins/ClashArena/Message"));
    }

    private void createFolder(String folderName) {
        File folder = new File(getDataFolder(), folderName);
        if (!folder.exists())
            if (!folder.mkdir()) ClashArenaLogger.error("Folder " + folderName + " could not be created.");
    }

    private void saveResourceToFolder(String resourceName, File targetFolder) {
        try {
            File targetFile = new File(targetFolder, resourceName);
            if (targetFile.exists()) {
                return;
            }
            if (!targetFolder.exists()) targetFolder.mkdirs();
            InputStream source = getResource(resourceName);
            if (source == null) {
                getLogger().warning("Resource " + resourceName + " not found in the plugin's resources.");
                return;
            }
            Files.copy(source, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            source.close();
        } catch (IOException exception) {
            getLogger().warning("Could not copy " + resourceName + " to " + targetFolder.getAbsolutePath() + ": " + exception.getMessage());
        }
    }
}
