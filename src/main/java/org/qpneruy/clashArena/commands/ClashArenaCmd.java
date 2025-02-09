package org.qpneruy.clashArena.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.qpneruy.clashArena.ClashArena;
import org.qpneruy.clashArena.menu.enums.Menu;

import java.util.Objects;

public class ClashArenaCmd implements CommandExecutor {

    public ClashArenaCmd(ClashArena plugin) {
        Objects.requireNonNull(plugin.getCommand("ClashArena")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        System.out.println("Open main menu");
        if (!(sender instanceof Player player)) return true;
        if (!sender.hasPermission("clasharena.use")) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cInvalid argument. Usage: /ClashArena <leader/member/setting/request>");
            return true;
        }

        switch (args[0]) {
            case "leader" -> ClashArena.menuManager.openMenu(player, Menu.LEADER);
            case "member" -> ClashArena.menuManager.openMenu(player, Menu.MEMBER);
            case "setting" -> ClashArena.menuManager.openMenu(player, Menu.SETTING);
            case "request" -> ClashArena.menuManager.openMenu(player, Menu.REQUEST);
            case "main" -> ClashArena.menuManager.openMenu(player, Menu.MAIN);
            default -> {
                player.sendMessage("§cInvalid argument. Usage: /ClashArena <leader/member/setting/request>");
                return true;
            }
        }
        return true;
    }
}