package com.suzunayui.recyclegacha;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GachaCommand implements CommandExecutor {
    
    private final RecycleGacha plugin;
    
    public GachaCommand(RecycleGacha plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            Component message = LegacyComponentSerializer.legacySection().deserialize("§cこのコマンドはプレイヤーのみ使用できます。");
            sender.sendMessage(message);
            return true;
        }
        
        if (!player.hasPermission("recyclegacha.admin")) {
            Component message = LegacyComponentSerializer.legacySection().deserialize("§c権限がありません。");
            player.sendMessage(message);
            return true;
        }
        
        if (args.length == 0) {
            Component message1 = LegacyComponentSerializer.legacySection().deserialize("§6=== RecycleGacha Help ===");
            Component message2 = LegacyComponentSerializer.legacySection().deserialize("§e/rg reload §7- 設定を再読み込み");
            Component message3 = LegacyComponentSerializer.legacySection().deserialize("§e/rg info §7- 見ているホッパーのポイントを確認");
            player.sendMessage(message1);
            player.sendMessage(message2);
            player.sendMessage(message3);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.reloadConfig();
                Component message = LegacyComponentSerializer.legacySection().deserialize("§a設定を再読み込みしました。");
                player.sendMessage(message);
                break;
            case "info":
                Component infoMsg = LegacyComponentSerializer.legacySection().deserialize("§cこの機能は未実装です。");
                player.sendMessage(infoMsg);
                break;
            default:
                Component errorMsg = LegacyComponentSerializer.legacySection().deserialize("§c不明なコマンドです。");
                player.sendMessage(errorMsg);
        }
        
        return true;
    }
}
