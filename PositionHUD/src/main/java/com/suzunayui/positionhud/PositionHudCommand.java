package com.suzunayui.positionhud;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PositionHudCommand implements CommandExecutor, TabCompleter {
    
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
        
        if (args.length == 0) {
            plugin.toggle(player);
            
            if (plugin.isEnabled(player.getUniqueId())) {
                player.sendMessage("§a座標表示をONにしました。");
            } else {
                player.sendMessage("§c座標表示をOFFにしました。");
            }
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("§cプレイヤーが見つかりません: " + args[0]);
                return true;
            }
            
            Location loc = target.getLocation();
            int x = loc.getBlockX();
            int y = loc.getBlockY();
            int z = loc.getBlockZ();
            String world = loc.getWorld().getName();
            
            player.sendMessage("§6=== " + target.getName() + " の位置 ===");
            player.sendMessage("§aワールド: §f" + world);
            player.sendMessage("§a座標: §fX: " + x + "  Y: " + y + "  Z: " + z);
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(input))
                .sorted()
                .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
