package com.suzunayui.positionhud;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PositionHudCommand implements CommandExecutor {
    
    private final PositionHUD plugin;
    
    public PositionHudCommand(PositionHUD plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cこのコマンドはプレイヤーのみ使用できます。");
            return true;
        }
        
        plugin.toggle(player);
        
        if (plugin.isEnabled(player.getUniqueId())) {
            player.sendMessage("§a座標表示をONにしました。");
        } else {
            player.sendMessage("§c座標表示をOFFにしました。");
        }
        
        return true;
    }
}
