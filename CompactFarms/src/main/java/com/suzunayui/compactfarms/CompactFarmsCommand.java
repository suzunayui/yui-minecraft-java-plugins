package com.suzunayui.compactfarms;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CompactFarmsCommand implements CommandExecutor {
    
    private final CompactFarms plugin;
    
    public CompactFarmsCommand(CompactFarms plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cこのコマンドはプレイヤーのみ使用できます。");
            return true;
        }
        
        if (!player.hasPermission("compactfarms.admin")) {
            player.sendMessage("§c権限がありません。");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage("§6=== CompactFarms Help ===");
            player.sendMessage("§e/compactfarms reload §7- 設定を再読み込み");
            player.sendMessage("§e/compactfarms info §7- 自分のコンテナ数を確認");
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.reloadConfig();
                player.sendMessage("§a設定を再読み込みしました。");
                break;
            case "info":
                int maxPerType = plugin.getConfig().getInt("global.max-per-type", 1);
                int ironCount = ResourceGenerator.getInstance().getContainerCountOfType(player.getUniqueId(), org.bukkit.Material.WHITE_SHULKER_BOX);
                int emeraldCount = ResourceGenerator.getInstance().getContainerCountOfType(player.getUniqueId(), org.bukkit.Material.GREEN_SHULKER_BOX);
                int gunpowderCount = ResourceGenerator.getInstance().getContainerCountOfType(player.getUniqueId(), org.bukkit.Material.GRAY_SHULKER_BOX);
                int expCount = ResourceGenerator.getInstance().getContainerCountOfType(player.getUniqueId(), org.bukkit.Material.LIME_SHULKER_BOX);
                player.sendMessage("§6=== CompactFarms 情報 ===");
                player.sendMessage("§a鉄CompactFarms: " + ironCount + "/" + maxPerType);
                player.sendMessage("§aエメラルドCompactFarms: " + emeraldCount + "/" + maxPerType);
                player.sendMessage("§a火薬CompactFarms: " + gunpowderCount + "/" + maxPerType);
                player.sendMessage("§a経験値CompactFarms: " + expCount + "/" + maxPerType);
                break;
            default:
                player.sendMessage("§c不明なコマンドです。");
        }
        
        return true;
    }
}
