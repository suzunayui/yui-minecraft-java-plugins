package com.suzunayui.tinyprotect;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.sql.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class DatabaseManager {

    private final TinyProtect plugin;
    private Connection connection;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.of("Asia/Tokyo"));

    public DatabaseManager(TinyProtect plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
            String url = "jdbc:sqlite:" + new File(dataFolder, "tinyprotect.db").getAbsolutePath();
            connection = DriverManager.getConnection(url);
            createTables();
            plugin.getLogger().info("Database connected successfully.");
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to connect to database: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to disconnect database: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS logs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    timestamp INTEGER NOT NULL,
                    player_uuid TEXT NOT NULL,
                    player_name TEXT NOT NULL,
                    action_type TEXT NOT NULL,
                    world TEXT NOT NULL,
                    x INTEGER NOT NULL,
                    y INTEGER NOT NULL,
                    z INTEGER NOT NULL,
                    block_type TEXT,
                    item_type TEXT,
                    item_amount INTEGER DEFAULT 0,
                    extra_data TEXT
                )
            """);
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_logs_location ON logs(world, x, y, z)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_logs_player ON logs(player_uuid)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_logs_timestamp ON logs(timestamp)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_logs_action ON logs(action_type)");
        }
    }

    public CompletableFuture<Void> logBlockBreak(UUID playerUuid, String playerName, Location loc, Material blockType) {
        return logAction(playerUuid, playerName, "BLOCK_BREAK", loc, blockType.name(), null, 0, null);
    }

    public CompletableFuture<Void> logBlockPlace(UUID playerUuid, String playerName, Location loc, Material blockType) {
        return logAction(playerUuid, playerName, "BLOCK_PLACE", loc, blockType.name(), null, 0, null);
    }

    public CompletableFuture<Void> logContainerRemove(UUID playerUuid, String playerName, Location loc, Material containerType, ItemStack item) {
        return logAction(playerUuid, playerName, "CONTAINER_REMOVE", loc, containerType.name(), item.getType().name(), item.getAmount(), null);
    }

    public CompletableFuture<Void> logContainerAdd(UUID playerUuid, String playerName, Location loc, Material containerType, ItemStack item) {
        return logAction(playerUuid, playerName, "CONTAINER_ADD", loc, containerType.name(), item.getType().name(), item.getAmount(), null);
    }

    public CompletableFuture<Void> logItemPickup(UUID playerUuid, String playerName, Location loc, ItemStack item) {
        return logAction(playerUuid, playerName, "ITEM_PICKUP", loc, null, item.getType().name(), item.getAmount(), null);
    }

    public CompletableFuture<Void> logItemDrop(UUID playerUuid, String playerName, Location loc, ItemStack item) {
        return logAction(playerUuid, playerName, "ITEM_DROP", loc, null, item.getType().name(), item.getAmount(), null);
    }

    public CompletableFuture<Void> logPlayerKill(UUID playerUuid, String playerName, Location loc, String killerName, String cause) {
        return logAction(playerUuid, playerName, "PLAYER_DEATH", loc, null, null, 0, "killer=" + killerName + ";cause=" + cause);
    }

    private CompletableFuture<Void> logAction(UUID playerUuid, String playerName, String actionType,
                                               Location loc, String blockType, String itemType, int amount, String extraData) {
        return CompletableFuture.runAsync(() -> {
            try {
                String sql = "INSERT INTO logs (timestamp, player_uuid, player_name, action_type, world, x, y, z, block_type, item_type, item_amount, extra_data) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setLong(1, System.currentTimeMillis());
                    ps.setString(2, playerUuid.toString());
                    ps.setString(3, playerName);
                    ps.setString(4, actionType);
                    ps.setString(5, loc.getWorld().getName());
                    ps.setInt(6, loc.getBlockX());
                    ps.setInt(7, loc.getBlockY());
                    ps.setInt(8, loc.getBlockZ());
                    ps.setString(9, blockType);
                    ps.setString(10, itemType);
                    ps.setInt(11, amount);
                    ps.setString(12, extraData);
                    ps.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to log action: " + e.getMessage());
            }
        });
    }

    public List<LogEntry> searchByLocation(String world, int x, int y, int z, int radius, int limit) {
        List<LogEntry> results = new ArrayList<>();
        try {
            String sql = """
                SELECT * FROM logs
                WHERE world = ? AND x BETWEEN ? AND ? AND y BETWEEN ? AND ? AND z BETWEEN ? AND ?
                ORDER BY timestamp DESC LIMIT ?
            """;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, world);
                ps.setInt(2, x - radius);
                ps.setInt(3, x + radius);
                ps.setInt(4, y - radius);
                ps.setInt(5, y + radius);
                ps.setInt(6, z - radius);
                ps.setInt(7, z + radius);
                ps.setInt(8, limit);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        results.add(readLogEntry(rs));
                    }
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to search logs: " + e.getMessage());
        }
        return results;
    }

    public List<LogEntry> searchByPlayer(String playerName, int limit, Long sinceTimestamp) {
        List<LogEntry> results = new ArrayList<>();
        try {
            String sql;
            if (sinceTimestamp != null) {
                sql = "SELECT * FROM logs WHERE player_name = ? AND timestamp >= ? ORDER BY timestamp DESC LIMIT ?";
            } else {
                sql = "SELECT * FROM logs WHERE player_name = ? ORDER BY timestamp DESC LIMIT ?";
            }
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, playerName);
                int idx = 2;
                if (sinceTimestamp != null) {
                    ps.setLong(idx++, sinceTimestamp);
                }
                ps.setInt(idx, limit);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        results.add(readLogEntry(rs));
                    }
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to search logs by player: " + e.getMessage());
        }
        return results;
    }

    public List<LogEntry> searchByLocationAndAction(String world, int x, int y, int z, int radius, String actionType, int limit) {
        List<LogEntry> results = new ArrayList<>();
        try {
            String sql = """
                SELECT * FROM logs
                WHERE world = ? AND x BETWEEN ? AND ? AND y BETWEEN ? AND ? AND z BETWEEN ? AND ? AND action_type = ?
                ORDER BY timestamp DESC LIMIT ?
            """;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, world);
                ps.setInt(2, x - radius);
                ps.setInt(3, x + radius);
                ps.setInt(4, y - radius);
                ps.setInt(5, y + radius);
                ps.setInt(6, z - radius);
                ps.setInt(7, z + radius);
                ps.setString(8, actionType);
                ps.setInt(9, limit);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        results.add(readLogEntry(rs));
                    }
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to search logs: " + e.getMessage());
        }
        return results;
    }

    public List<LogEntry> searchNearby(String world, int centerX, int centerY, int centerZ, int radius, String blockType, String actionFilter, int limit) {
        List<LogEntry> results = new ArrayList<>();
        try {
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT *, ((x - ?) * (x - ?) + (y - ?) * (y - ?) + (z - ?) * (z - ?)) as dist_sq ");
            sqlBuilder.append("FROM logs ");
            sqlBuilder.append("WHERE world = ? AND x BETWEEN ? AND ? AND y BETWEEN ? AND ? AND z BETWEEN ? AND ? ");

            List<Object> params = new ArrayList<>();
            params.add(centerX);
            params.add(centerX);
            params.add(centerY);
            params.add(centerY);
            params.add(centerZ);
            params.add(centerZ);
            params.add(world);
            params.add(centerX - radius);
            params.add(centerX + radius);
            params.add(centerY - radius);
            params.add(centerY + radius);
            params.add(centerZ - radius);
            params.add(centerZ + radius);

            if (blockType != null) {
                sqlBuilder.append("AND (block_type = ? OR item_type = ?) ");
                params.add(blockType);
                params.add(blockType);
            }

            if (actionFilter != null) {
                if (actionFilter.contains(",")) {
                    String[] actions = actionFilter.split(",");
                    sqlBuilder.append("AND action_type IN (");
                    for (int i = 0; i < actions.length; i++) {
                        sqlBuilder.append("?");
                        if (i < actions.length - 1) sqlBuilder.append(",");
                        params.add(actions[i]);
                    }
                    sqlBuilder.append(") ");
                } else {
                    sqlBuilder.append("AND action_type = ? ");
                    params.add(actionFilter);
                }
            }

            sqlBuilder.append("ORDER BY dist_sq ASC LIMIT ?");
            params.add(limit);

            try (PreparedStatement ps = connection.prepareStatement(sqlBuilder.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    Object param = params.get(i);
                    if (param instanceof Integer) {
                        ps.setInt(i + 1, (Integer) param);
                    } else if (param instanceof String) {
                        ps.setString(i + 1, (String) param);
                    }
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        results.add(readLogEntry(rs));
                    }
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to search nearby logs: " + e.getMessage());
        }
        return results;
    }

    public int rollbackPlayer(String playerName, String world, long sinceTimestamp) {
        int count = 0;
        try {
            String sql = "SELECT * FROM logs WHERE player_name = ? AND world = ? AND timestamp >= ? ORDER BY timestamp DESC";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, playerName);
                ps.setString(2, world);
                ps.setLong(3, sinceTimestamp);
                try (ResultSet rs = ps.executeQuery()) {
                    List<LogEntry> entries = new ArrayList<>();
                    while (rs.next()) {
                        entries.add(readLogEntry(rs));
                    }

                    for (LogEntry entry : entries) {
                        Location loc = new Location(
                                plugin.getServer().getWorld(entry.world),
                                entry.x, entry.y, entry.z
                        );

                        if ("BLOCK_PLACE".equals(entry.actionType)) {
                            loc.getBlock().setType(Material.AIR);
                            count++;
                        } else if ("BLOCK_BREAK".equals(entry.actionType) && entry.blockType != null) {
                            try {
                                Material mat = Material.valueOf(entry.blockType);
                                loc.getBlock().setType(mat);
                                count++;
                            } catch (IllegalArgumentException ignored) {}
                        }
                    }
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to rollback: " + e.getMessage());
        }
        return count;
    }

    public int purgeOldLogs(int daysToKeep) {
        int count = 0;
        try {
            long cutoff = System.currentTimeMillis() - ((long) daysToKeep * 24 * 60 * 60 * 1000);
            String sql = "DELETE FROM logs WHERE timestamp < ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setLong(1, cutoff);
                count = ps.executeUpdate();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to purge logs: " + e.getMessage());
        }
        return count;
    }

    public Map<String, Integer> getStats() {
        Map<String, Integer> stats = new LinkedHashMap<>();
        try {
            String sql = "SELECT action_type, COUNT(*) as cnt FROM logs GROUP BY action_type ORDER BY cnt DESC";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    stats.put(rs.getString("action_type"), rs.getInt("cnt"));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to get stats: " + e.getMessage());
        }
        return stats;
    }

    private LogEntry readLogEntry(ResultSet rs) throws SQLException {
        return new LogEntry(
                rs.getLong("timestamp"),
                rs.getString("player_uuid"),
                rs.getString("player_name"),
                rs.getString("action_type"),
                rs.getString("world"),
                rs.getInt("x"),
                rs.getInt("y"),
                rs.getInt("z"),
                rs.getString("block_type"),
                rs.getString("item_type"),
                rs.getInt("item_amount"),
                rs.getString("extra_data")
        );
    }

    public String formatTimestamp(long timestamp) {
        return formatter.format(Instant.ofEpochMilli(timestamp));
    }

    public static class LogEntry {
        public final long timestamp;
        public final String playerUuid;
        public final String playerName;
        public final String actionType;
        public final String world;
        public final int x, y, z;
        public final String blockType;
        public final String itemType;
        public final int itemAmount;
        public final String extraData;

        public LogEntry(long timestamp, String playerUuid, String playerName, String actionType,
                        String world, int x, int y, int z, String blockType, String itemType,
                        int itemAmount, String extraData) {
            this.timestamp = timestamp;
            this.playerUuid = playerUuid;
            this.playerName = playerName;
            this.actionType = actionType;
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.blockType = blockType;
            this.itemType = itemType;
            this.itemAmount = itemAmount;
            this.extraData = extraData;
        }
    }
}
