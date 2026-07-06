package com.suzunayui.tinyprotect;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TinyProtectTabCompleter implements TabCompleter {

    private static final List<String> SUBCOMMANDS = List.of("inspect", "search", "pos", "near", "rollback", "rollback-area", "clearfire", "clearwater", "clearlava", "status", "purge", "help");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return filterStartingWith(args[0], SUBCOMMANDS);
        }
        
        // rollback と search の2番目の引数にプレイヤー名を補完
        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("rollback") || subCommand.equals("search")) {
                List<String> playerNames = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    playerNames.add(player.getName());
                }
                return filterStartingWith(args[1], playerNames);
            }
        }
        
        return new ArrayList<>();
    }

    private List<String> filterStartingWith(String input, List<String> options) {
        List<String> result = new ArrayList<>();
        String lower = input.toLowerCase();
        for (String option : options) {
            if (option.toLowerCase().startsWith(lower)) {
                result.add(option);
            }
        }
        return result;
    }
}
