package com.suzunayui.tinyprotect;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TinyProtectTabCompleter implements TabCompleter {

    private static final List<String> SUBCOMMANDS = List.of("inspect", "search", "pos", "near", "rollback", "rollback-area", "status", "purge", "help");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return filterStartingWith(args[0], SUBCOMMANDS);
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
