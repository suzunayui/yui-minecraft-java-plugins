package com.suzunayui.containersearch;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ContainerSearchCommand implements CommandExecutor, TabCompleter {
    
    private final ContainerScanner scanner;
    
    public ContainerSearchCommand(ContainerSearch plugin) {
        this.scanner = new ContainerScanner(plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cこのコマンドはプレイヤーのみ使用できます。");
            return true;
        }
        
        if (!player.hasPermission("containersearch.use")) {
            player.sendMessage("§c権限がありません。");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage("§6=== ContainerSearch Help ===");
            player.sendMessage("§e/cs <アイテム名> §7- 近くのコンテナからアイテムを検索");
            return true;
        }
        
        String itemName = args[0].toUpperCase();
        Material targetItem;
        
        try {
            targetItem = Material.valueOf(itemName);
        } catch (IllegalArgumentException e) {
            player.sendMessage("§c不明なアイテム名です: " + args[0]);
            return true;
        }
        
        if (!targetItem.isItem()) {
            player.sendMessage("§cそのアイテムは検索できません: " + args[0]);
            return true;
        }
        
        List<ContainerScanner.SearchResult> results = scanner.search(player, targetItem);
        
        if (results.isEmpty()) {
            player.sendMessage("§c" + getItemDisplayName(targetItem) + "は見つかりませんでした。");
            return true;
        }
        
        player.sendMessage("§6=== " + getItemDisplayName(targetItem) + " の検索結果 (" + results.size() + "件) ===");
        
        for (ContainerScanner.SearchResult result : results) {
            String path = String.join(" §7> ", result.getPath());
            int x = result.getLocation().getBlockX();
            int y = result.getLocation().getBlockY();
            int z = result.getLocation().getBlockZ();
            
            player.sendMessage("§a" + path + " §ex" + x + " y" + y + " z" + z + " §f(" + result.getAmount() + "個)");
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String input = args[0].toUpperCase();
            return Arrays.stream(Material.values())
                .filter(Material::isItem)
                .map(Material::name)
                .filter(name -> name.startsWith(input))
                .sorted()
                .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
    
    private String getItemDisplayName(Material material) {
        String name = material.name().toLowerCase();
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        
        for (char c : name.toCharArray()) {
            if (c == '_') {
                result.append(' ');
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    result.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    result.append(c);
                }
            }
        }
        
        return result.toString();
    }
}
