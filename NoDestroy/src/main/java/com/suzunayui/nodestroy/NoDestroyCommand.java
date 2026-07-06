package com.suzunayui.nodestroy;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NoDestroyCommand implements CommandExecutor, TabCompleter {

    private final NoDestroy plugin;

    public NoDestroyCommand(NoDestroy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("§cこのコマンドはOPのみ使用できます。");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            handleList(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("fire")) {
            if (args.length < 2) {
                sender.sendMessage("§c使用法: /nd fire <true|false>");
                return true;
            }
            handleFire(sender, args[1]);
            return true;
        }

        if (args[0].equalsIgnoreCase("lava")) {
            if (args.length < 2) {
                sender.sendMessage("§c使用法: /nd lava <true|false>");
                return true;
            }
            handleLava(sender, args[1]);
            return true;
        }

        if (!args[0].equalsIgnoreCase("use")) {
            sendHelp(sender);
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("§c使用法: /nd use <プレイヤー名> <true|false>");
            return true;
        }

        String playerName = args[1];
        String action = args[2].toLowerCase();

        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage("§cプレイヤー §e" + playerName + " §cが見つかりません（オフラインのプレイヤーは指定できません）。");
            return true;
        }

        if (action.equals("true")) {
            plugin.addAllowedPlayer(target.getUniqueId());
            sender.sendMessage("§a" + target.getName() + " の使用を許可しました。");
            target.sendMessage("§aNoDestroyの使用が許可されました。");
        } else if (action.equals("false")) {
            plugin.removeAllowedPlayer(target.getUniqueId());
            sender.sendMessage("§e" + target.getName() + " の許可を取り消しました。");
            target.sendMessage("§eNoDestroyの使用許可が取り消されました。");
        } else {
            sender.sendMessage("§c使用法: /nd use <プレイヤー名> <true|false>");
        }

        return true;
    }

    private void handleList(CommandSender sender) {
        sender.sendMessage("§6=== NoDestroy 許可リスト ===");
        int count = 0;
        for (java.util.UUID uuid : plugin.getAllowedPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            String name = player != null ? player.getName() : uuid.toString();
            sender.sendMessage("§e" + name);
            count++;
        }
        sender.sendMessage("§7合計: §e" + count + "§7人");
    }

    private void handleFire(CommandSender sender, String value) {
        if (value.equalsIgnoreCase("true")) {
            plugin.setFireSpreadAllowed(true);
            sender.sendMessage("§a火の燃え広がりを許可しました。");
        } else if (value.equalsIgnoreCase("false")) {
            plugin.setFireSpreadAllowed(false);
            sender.sendMessage("§e火の燃え広がりを禁止しました。ブロックが燃え尽きなくなります。");
        } else {
            sender.sendMessage("§c使用法: /nd fire <true|false>");
        }
    }

    private void handleLava(CommandSender sender, String value) {
        if (value.equalsIgnoreCase("true")) {
            plugin.setLavaDestroyAllowed(true);
            sender.sendMessage("§a溶岩によるブロック破壊を許可しました。");
        } else if (value.equalsIgnoreCase("false")) {
            plugin.setLavaDestroyAllowed(false);
            sender.sendMessage("§e溶岩によるブロック破壊を禁止しました。");
        } else {
            sender.sendMessage("§c使用法: /nd lava <true|false>");
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6=== NoDestroy コマンド ===");
        sender.sendMessage("§e/nd use <プレイヤー名> true §7- 使用を許可する");
        sender.sendMessage("§e/nd use <プレイヤー名> false §7- 許可を取り消す");
        sender.sendMessage("§e/nd fire true §7- 火の燃え広がりを許可する");
        sender.sendMessage("§e/nd fire false §7- 火の燃え広がりを禁止する（ブロックが燃え尽きない）");
        sender.sendMessage("§e/nd lava true §7- 溶岩によるブロック破壊を許可する");
        sender.sendMessage("§e/nd lava false §7- 溶岩によるブロック破壊を禁止する");
        sender.sendMessage("§e/nd list §7- 許可リストを表示");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.isOp()) return new ArrayList<>();

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0].toLowerCase();
            if ("use".startsWith(input)) completions.add("use");
            if ("fire".startsWith(input)) completions.add("fire");
            if ("lava".startsWith(input)) completions.add("lava");
            if ("list".startsWith(input)) completions.add("list");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("use")) {
            String input = args[1].toLowerCase();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(input)) {
                    completions.add(player.getName());
                }
            }
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("fire") || args[0].equalsIgnoreCase("lava"))) {
            String input = args[1].toLowerCase();
            if ("true".startsWith(input)) completions.add("true");
            if ("false".startsWith(input)) completions.add("false");
        } else if (args.length == 3 && args[0].equalsIgnoreCase("use")) {
            String input = args[2].toLowerCase();
            if ("true".startsWith(input)) completions.add("true");
            if ("false".startsWith(input)) completions.add("false");
        }

        return completions;
    }
}
