package org.qpneruy.clashArena.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.qpneruy.clashArena.ClashArena;

import java.util.*;

public class ClashArenaCompleter implements TabCompleter {

    public ClashArenaCompleter(ClashArena plugin) {
        Objects.requireNonNull(plugin.getCommand("ClashArena")).setTabCompleter(this);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {

        return null;
    }



    private List<String> suggest(String input, List<String> keywords) {
        List<String> result = new ArrayList<>();
        String lowerInput = input.toLowerCase();

        for (String keyword : keywords)
            if (keyword.contains(lowerInput)) result.add(keyword);

        return result;
    }
}
