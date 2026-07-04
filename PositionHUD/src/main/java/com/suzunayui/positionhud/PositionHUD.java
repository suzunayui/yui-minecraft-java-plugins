package com.suzunayui.positionhud;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class PositionHUD extends JavaPlugin implements Listener {
    
    private static PositionHUD instance;
    private final Set<UUID> enabledPlayers = new HashSet<>();
    private BukkitRunnable displayTask;
    private File dataFile;
    private FileConfiguration dataConfig;
    
    @Override
    public void onEnable() {
        instance = this;
        
        if (getCommand("positionhud") != null) {
            PositionHudCommand executor = new PositionHudCommand(this);
            getCommand("positionhud").setExecutor(executor);
            getCommand("positionhud").setTabCompleter(executor);
        }
        
        loadData();
        
        startDisplayTask();
        
        getServer().getPluginManager().registerEvents(this, this);
        
        getLogger().info("PositionHUD has been enabled!");
    }
    
    @Override
    public void onDisable() {
        if (displayTask != null) {
            displayTask.cancel();
        }
        saveData();
        getLogger().info("PositionHUD has been disabled!");
    }
    
    private void loadData() {
        dataFile = new File(getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            return;
        }
        
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        List<String> uuidStrings = dataConfig.getStringList("enabled-players");
        for (String uuidStr : uuidStrings) {
            try {
                enabledPlayers.add(UUID.fromString(uuidStr));
            } catch (IllegalArgumentException e) {
                getLogger().warning("Invalid UUID in data.yml: " + uuidStr);
            }
        }
    }
    
    private void saveData() {
        if (dataConfig == null) {
            dataConfig = new YamlConfiguration();
        }
        
        List<String> uuidStrings = enabledPlayers.stream()
            .map(UUID::toString)
            .toList();
        dataConfig.set("enabled-players", uuidStrings);
        
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to save data.yml", e);
        }
    }
    
    private void startDisplayTask() {
        displayTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : enabledPlayers) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null && player.isOnline()) {
                        displayPosition(player);
                    }
                }
            }
        };
        displayTask.runTaskTimer(this, 0L, 5L);
    }
    
    private void displayPosition(Player player) {
        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();
        int z = player.getLocation().getBlockZ();
        String direction = getDirection(player.getLocation().getYaw());
        String time = getMinecraftTime(player);

        Component message = Component.text("X: " + x + "  Y: " + y + "  Z: " + z + "  [" + direction + "]  " + time)
            .color(NamedTextColor.WHITE);

        player.sendActionBar(message);
    }

    private String getMinecraftTime(Player player) {
        long time = player.getWorld().getTime();
        int hours = (int) ((time / 1000 + 6) % 24);
        int minutes = (int) ((time % 1000) * 60 / 1000);
        return String.format("%02d:%02d", hours, minutes);
    }
    
    private String getDirection(float yaw) {
        yaw = yaw % 360;
        if (yaw < 0) yaw += 360;
        
        if (yaw >= 337.5 || yaw < 22.5) {
            return " 南 ";
        } else if (yaw >= 22.5 && yaw < 67.5) {
            return "南西";
        } else if (yaw >= 67.5 && yaw < 112.5) {
            return " 西 ";
        } else if (yaw >= 112.5 && yaw < 157.5) {
            return "北西";
        } else if (yaw >= 157.5 && yaw < 202.5) {
            return " 北 ";
        } else if (yaw >= 202.5 && yaw < 247.5) {
            return "北東";
        } else if (yaw >= 247.5 && yaw < 292.5) {
            return " 東 ";
        } else if (yaw >= 292.5 && yaw < 337.5) {
            return "南東";
        }
        return "？？";
    }
    
    public boolean isEnabled(UUID playerId) {
        return enabledPlayers.contains(playerId);
    }
    
    public void toggle(Player player) {
        UUID uuid = player.getUniqueId();
        if (enabledPlayers.contains(uuid)) {
            enabledPlayers.remove(uuid);
            player.sendActionBar(Component.empty());
        } else {
            enabledPlayers.add(uuid);
        }
        saveData();
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (enabledPlayers.contains(player.getUniqueId())) {
            player.sendMessage("§a座標表示がONになっています。");
        }
    }
    
    public static PositionHUD getInstance() {
        return instance;
    }
}
