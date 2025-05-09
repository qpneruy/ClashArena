package org.qpneruy.clashArena.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.qpneruy.clashArena.ClashArena;

import java.util.Objects;

public class ClashArenaCmd implements CommandExecutor {

    public ClashArenaCmd(ClashArena plugin) {
        Objects.requireNonNull(plugin.getCommand("ClashArena")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args[0].equalsIgnoreCase("DeleteWorld") && sender instanceof Player) {
            if (!sender.hasPermission("clasharena.deleteworld")) {
                sender.sendMessage("§cYou don't have permission to use this command.");
                return true;
            }
//            World currentWorld = ((Player) sender).getWorld();
//            ClashArena.instance.getArenaManager().deleteArena();
//            sender.sendMessage("§aWorld " + worldName + " deleted successfully.");
            return true;
        }
        Player player = (Player) sender;
        ClashArena.instance.getArenaManager().createArena(player, "nether_arena")
                .thenAccept(result -> {
                    if (result.success()) {
                        Bukkit.getScheduler().runTask(ClashArena.instance, () -> {
                            player.teleport(result.spawnLocation());
                            player.sendMessage("Arena created successfully!");
                        });
                    } else {
                        player.sendMessage("Failed to create arena!");
                    }
                });

//        System.out.println("Open main menu");
//        if (!(sender instanceof Player player)) return true;
//        if (!sender.hasPermission("clasharena.use")) {
//            sender.sendMessage("§cYou don't have permission to use this command.");
//            return true;
//        }

//        ClashArena.instance.getMenuManager().openMenu(player, Menu.UNDEFINED);
//        return true;
//        switch (args[0]) {
//            case "up" -> {
//                ClashArena.instance.getArenaPlayerManager().computeArenaPlayer(player.getUniqueId()).updateStar(1);
//            }
//            case "leader" -> ClashArena.instance.getMenuManager().openMenu(player, Menu.LEADER);
//            case "member" -> ClashArena.instance.getMenuManager().openMenu(player, Menu.MEMBER);
//            case "setting" -> ClashArena.instance.getMenuManager().openMenu(player, Menu.SETTING);
//            case "request" -> ClashArena.instance.getMenuManager().openMenu(player, Menu.REQUEST);
//            case "main" -> ClashArena.instance.getMenuManager().openMenu(player, Menu.MAIN);
//            default -> {
//                player.sendMessage("§cInvalid argument. Usage: /ClashArena <leader/member/setting/request>");
//                return true;
//            }
//        }
    return true;
}
    }