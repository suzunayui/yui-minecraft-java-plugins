package com.suzunayui.ultimatelibrarian;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LibrarianCommand implements CommandExecutor, TabCompleter {
    
    private final UltimateLibrarian plugin;
    
    public LibrarianCommand(UltimateLibrarian plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("このコマンドはプレイヤーのみ使用できます。").color(NamedTextColor.RED));
            return true;
        }
        
        if (!player.hasPermission("ultimatelibrarian.use")) {
            player.sendMessage(Component.text("権限がありません。").color(NamedTextColor.RED));
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(Component.text("=== UltimateLibrarian Help ===").color(NamedTextColor.GOLD));
            player.sendMessage(Component.text("/ul tool ").color(NamedTextColor.YELLOW)
                .append(Component.text("- 道具司書を召喚").color(NamedTextColor.GRAY)));
            player.sendMessage(Component.text("/ul weapon ").color(NamedTextColor.YELLOW)
                .append(Component.text("- 武器司書を召喚").color(NamedTextColor.GRAY)));
            player.sendMessage(Component.text("/ul armor ").color(NamedTextColor.YELLOW)
                .append(Component.text("- 防具司書を召喚").color(NamedTextColor.GRAY)));
            player.sendMessage(Component.text("/ul ranged ").color(NamedTextColor.YELLOW)
                .append(Component.text("- 遠距離司書を召喚").color(NamedTextColor.GRAY)));
            player.sendMessage(Component.text("/ul remove ").color(NamedTextColor.YELLOW)
                .append(Component.text("- 全ての司書NPCを削除").color(NamedTextColor.GRAY)));
            return true;
        }
        
        String type = args[0].toLowerCase();
        
        switch (type) {
            case "tool", "weapon", "armor", "ranged" -> {
                plugin.getLibrarianManager().spawnNPC(player, type);
            }
            case "remove" -> {
                plugin.getLibrarianManager().removeNPCs(player);
            }
            default -> {
                player.sendMessage(Component.text("不明なコマンドです。/ul でヘルプを表示します。").color(NamedTextColor.RED));
            }
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            List<String> options = Arrays.asList("tool", "weapon", "armor", "ranged", "remove");
            return options.stream()
                .filter(option -> option.startsWith(input))
                .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
