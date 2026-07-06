package com.suzunayui.tinyprotect;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class TinyProtectCommand implements CommandExecutor {

    private final TinyProtect plugin;

    public TinyProtectCommand(TinyProtect plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("tinyprotect.admin")) {
            sender.sendMessage("§cこのコマンドを使用する権限がありません。");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "inspect", "i" -> handleInspect(sender);
            case "search", "s" -> handleSearch(sender, args);
            case "pos", "p" -> handlePos(sender, args);
            case "near", "n" -> handleNear(sender, args);
            case "rollback", "rb" -> handleRollback(sender, args);
            case "rollback-area", "rba" -> handleRollbackArea(sender, args);
            case "status" -> handleStatus(sender);
            case "purge" -> handlePurge(sender, args);
            case "help", "?" -> sendHelp(sender);
            default -> {
                sender.sendMessage("§c不明なサブコマンドです。§e/ti help §cでヘルプを表示。");
            }
        }
        return true;
    }

    private void handleInspect(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cこのコマンドはプレイヤーのみ使用できます。");
            return;
        }

        plugin.toggleInspectMode(player.getUniqueId());
        if (plugin.isInspectMode(player.getUniqueId())) {
            player.sendMessage("§a調査モードを有効にしました。§7ブロックを右クリックして履歴を表示。");
        } else {
            player.sendMessage("§c調査モードを無効にしました。");
        }
    }

    private void handleSearch(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c使用法: /ti search <プレイヤー> [件数]");
            return;
        }

        String playerName = args[1];
        int limit = 10;
        if (args.length >= 3) {
            try {
                limit = Integer.parseInt(args[2]);
                limit = Math.max(1, Math.min(100, limit));
            } catch (NumberFormatException e) {
                sender.sendMessage("§c無効な件数です。デフォルト(10)を使用します。");
            }
        }

        DatabaseManager db = plugin.getDatabaseManager();
        List<DatabaseManager.LogEntry> entries = db.searchByPlayer(playerName, limit, null);

        sender.sendMessage("§6=== TinyProtect: Search '" + playerName + "' ===");
        if (entries.isEmpty()) {
            sender.sendMessage("§7プレイヤー §e" + playerName + " §7のログは見つかりませんでした。");
            return;
        }

        for (DatabaseManager.LogEntry entry : entries) {
            String time = db.formatTimestamp(entry.timestamp);
            String action = formatAction(entry.actionType);
            String detail = buildDetail(entry);
            String coords = "§7(" + entry.x + ", " + entry.y + ", " + entry.z + ")";
            sender.sendMessage("§7[" + time + "] " + action + " §e" + entry.playerName + " " + coords + detail);
        }
        sender.sendMessage("§e" + entries.size() + "§7件のログが見つかりました。");
    }

    private void handlePos(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§c使用法: /ti pos <x> <y> <z> [半径] [件数]");
            return;
        }

        int x, y, z;
        try {
            x = Integer.parseInt(args[1]);
            y = Integer.parseInt(args[2]);
            z = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§c無効な座標です。");
            return;
        }

        int radius = 1;
        int limit = 10;
        if (args.length >= 5) {
            try { radius = Integer.parseInt(args[4]); radius = Math.max(0, Math.min(50, radius)); } catch (NumberFormatException ignored) {}
        }
        if (args.length >= 6) {
            try { limit = Integer.parseInt(args[5]); limit = Math.max(1, Math.min(100, limit)); } catch (NumberFormatException ignored) {}
        }

        String world = "world";
        if (sender instanceof Player player && player.getWorld() != null) {
            world = player.getWorld().getName();
        }

        DatabaseManager db = plugin.getDatabaseManager();
        List<DatabaseManager.LogEntry> entries = db.searchByLocation(world, x, y, z, radius, limit);

        sender.sendMessage("§6=== TinyProtect: Pos (" + x + ", " + y + ", " + z + ") r=" + radius + " ===");
        if (entries.isEmpty()) {
            sender.sendMessage("§7この座標のログは見つかりませんでした。");
            return;
        }

        for (DatabaseManager.LogEntry entry : entries) {
            String time = db.formatTimestamp(entry.timestamp);
            String action = formatAction(entry.actionType);
            String detail = buildDetail(entry);
            sender.sendMessage("§7[" + time + "] " + action + " §e" + entry.playerName + detail);
        }
        sender.sendMessage("§e" + entries.size() + "§7件のログが見つかりました。");
    }

    private void handleNear(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cこのコマンドはプレイヤーのみ使用できます。");
            return;
        }

        int radius = 20;
        String blockType = null;
        String actionFilter = null;
        int limit = 5;

        for (int i = 1; i < args.length; i++) {
            String arg = args[i];
            if (arg.equalsIgnoreCase("break") || arg.equalsIgnoreCase("place") ||
                arg.equalsIgnoreCase("container") || arg.equalsIgnoreCase("pickup") ||
                arg.equalsIgnoreCase("drop") || arg.equalsIgnoreCase("death") ||
                arg.equalsIgnoreCase("kill")) {
                actionFilter = switch (arg.toLowerCase()) {
                    case "break" -> "BLOCK_BREAK";
                    case "place" -> "BLOCK_PLACE";
                    case "container" -> "CONTAINER_REMOVE,CONTAINER_ADD";
                    case "pickup" -> "ITEM_PICKUP";
                    case "drop" -> "ITEM_DROP";
                    case "death" -> "PLAYER_DEATH";
                    case "kill" -> "ENTITY_KILL";
                    default -> null;
                };
            } else {
                try {
                    int num = Integer.parseInt(arg);
                    if (num <= 100) {
                        if (radius == 20) {
                            radius = Math.max(1, num);
                        } else {
                            limit = Math.max(1, num);
                        }
                    }
                } catch (NumberFormatException e) {
                    blockType = arg.toUpperCase();
                }
            }
        }

        Location loc = player.getLocation();
        String world = loc.getWorld().getName();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        DatabaseManager db = plugin.getDatabaseManager();
        List<DatabaseManager.LogEntry> entries = db.searchNearby(world, x, y, z, radius, blockType, actionFilter, limit);

        StringBuilder titleBuilder = new StringBuilder("§6=== TinyProtect: Near (" + radius + "m)");
        if (blockType != null) titleBuilder.append(" - ").append(blockType);
        if (actionFilter != null) titleBuilder.append(" [").append(actionFilter.replace(",", "/")).append("]");
        titleBuilder.append(" ===");
        sender.sendMessage(titleBuilder.toString());

        if (entries.isEmpty()) {
            sender.sendMessage("§7付近にログは見つかりませんでした。");
            return;
        }

        for (DatabaseManager.LogEntry entry : entries) {
            String time = db.formatTimestamp(entry.timestamp);
            String action = formatAction(entry.actionType);
            String detail = buildDetail(entry);
            int dist = (int) Math.sqrt(Math.pow(entry.x - x, 2) + Math.pow(entry.y - y, 2) + Math.pow(entry.z - z, 2));
            String coords = "§7(" + entry.x + ", " + entry.y + ", " + entry.z + ") §8[" + dist + "m]";
            sender.sendMessage("§7[" + time + "] " + action + " §e" + entry.playerName + " " + coords + detail);
        }
        sender.sendMessage("§e" + entries.size() + "§7 entries. §8(Use /ti near ... <limit> to see more)");
    }

    private void handleRollback(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c使用法: /ti rollback <プレイヤー> [秒数]");
            return;
        }

        String playerName = args[1];
        int seconds = 3600;
        if (args.length >= 3) {
            try {
                seconds = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§c無効な秒数です。");
                return;
            }
        }

        String world = "world";
        if (sender instanceof Player player && player.getWorld() != null) {
            world = player.getWorld().getName();
        }

        long sinceTimestamp = System.currentTimeMillis() - ((long) seconds * 1000);
        int count = plugin.getDatabaseManager().rollbackPlayer(playerName, world, sinceTimestamp);

        sender.sendMessage("§e" + playerName + "§aの §e" + count + "§a件のブロック変更を §e" + seconds + "§a秒前からロールバックしました。");
    }

    private void handleRollbackArea(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cこのコマンドはプレイヤーのみ使用できます。");
            return;
        }

        int radius = 10;
        int seconds = 3600;

        if (args.length >= 2) {
            try {
                radius = Integer.parseInt(args[1]);
                radius = Math.max(1, Math.min(100, radius));
            } catch (NumberFormatException e) {
                sender.sendMessage("§c無効な半径です。");
                return;
            }
        }

        if (args.length >= 3) {
            try {
                seconds = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§c無効な秒数です。");
                return;
            }
        }

        Location loc = player.getLocation();
        String world = loc.getWorld().getName();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        long sinceTimestamp = System.currentTimeMillis() - ((long) seconds * 1000);
        int count = plugin.getDatabaseManager().rollbackArea(world, x, y, z, radius, sinceTimestamp);

        sender.sendMessage("§e" + radius + "§aブロック以内の §e" + count + "§aブロックを §e" + seconds + "§a秒前からロールバックしました。");
    }

    private void handleStatus(CommandSender sender) {
        DatabaseManager db = plugin.getDatabaseManager();
        Map<String, Integer> stats = db.getStats();

        sender.sendMessage("§6=== TinyProtect Status ===");
        int total = 0;
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            sender.sendMessage("§e" + entry.getKey() + ": §f" + entry.getValue());
            total += entry.getValue();
        }
        sender.sendMessage("§e合計ログ数: §f" + total);
    }

    private void handlePurge(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c使用法: /ti purge <日数>");
            return;
        }

        int days;
        try {
            days = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§c無効な日数です。");
            return;
        }

        int count = plugin.getDatabaseManager().purgeOldLogs(days);
        sender.sendMessage("§e" + days + "§a日以上経過したログを §e" + count + "§a件削除しました。");
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6=== TinyProtect ヘルプ ===");
        sender.sendMessage("§e/ti inspect §7- 調査モードの切り替え（ブロックを右クリック）");
        sender.sendMessage("§e/ti search <プレイヤー> [件数] §7- プレイヤーでログを検索");
        sender.sendMessage("§e/ti pos <x> <y> <z> [半径] [件数] §7- 座標でログを検索");
        sender.sendMessage("§e/ti near [半径] [ブロック] [break|place|container|pickup|drop|death|kill] [件数] §7- 付近のログを検索");
        sender.sendMessage("§e/ti rollback <プレイヤー> [秒数] §7- プレイヤーのブロック変更をロールバック");
        sender.sendMessage("§e/ti rollback-area [半径] [秒数] §7- 周囲のエリアをロールバック（爆発、火、液体）");
        sender.sendMessage("§e/ti status §7- データベース統計を表示");
        sender.sendMessage("§e/ti purge <日数> §7- 指定日数より古いログを削除");
    }

    private String formatAction(String actionType) {
        return switch (actionType) {
            case "BLOCK_BREAK" -> "§c[Broke]";
            case "BLOCK_PLACE" -> "§a[Placed]";
            case "CONTAINER_REMOVE" -> "§c[Took]";
            case "CONTAINER_ADD" -> "§a[Added]";
            case "ITEM_PICKUP" -> "§e[Pickup]";
            case "ITEM_DROP" -> "§7[Drop]";
            case "PLAYER_DEATH" -> "§4[Death]";
            case "ENTITY_KILL" -> "§4[Kill]";
            case "EXPLOSION" -> "§4[Exploded]";
            case "BLOCK_BURN" -> "§4[Burned]";
            case "LIQUID_DESTROY" -> "§4[Liquid]";
            default -> "§8[" + actionType + "]";
        };
    }

    private String buildDetail(DatabaseManager.LogEntry entry) {
        StringBuilder sb = new StringBuilder();
        if (entry.blockType != null) sb.append(" §f").append(entry.blockType);
        if (entry.itemType != null) sb.append(" §f").append(entry.itemType).append(" x").append(entry.itemAmount);
        if (entry.extraData != null) {
            String[] parts = entry.extraData.split(";");
            for (String part : parts) {
                if (part.startsWith("killer=")) {
                    sb.append(" §7by §c").append(part.substring(7));
                } else if (part.startsWith("cause=")) {
                    sb.append(" §7(").append(part.substring(6)).append(")");
                }
            }
        }
        return sb.toString();
    }
}
