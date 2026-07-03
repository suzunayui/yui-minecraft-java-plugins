package com.suzunayui.positionhud;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PositionHUD extends JavaPlugin {
    
    private static PositionHUD instance;
    private final Set<UUID> enabledPlayers = new HashSet<>();
    private BukkitRunnable displayTask;
    
    @Override
    public void onEnable() {
        instance = this;
        
        if (getCommand("positionhud") != null) {
            PositionHudCommand executor = new PositionHudCommand(this);
            getCommand("positionhud").setExecutor(executor);
            getCommand("positionhud").setTabCompleter(executor);
        }
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            enabledPlayers.add(player.getUniqueId());
        }
        
        startDisplayTask();
        
        getLogger().info("PositionHUD has been enabled!");
    }
    
    @Override
    public void onDisable() {
        if (displayTask != null) {
            displayTask.cancel();
        }
        getLogger().info("PositionHUD has been disabled!");
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
        
        Component message = Component.text("X: " + x + "  Y: " + y + "  Z: " + z)
            .color(NamedTextColor.WHITE);
        
        player.sendActionBar(message);
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
    }
    
    public static PositionHUD getInstance() {
        return instance;
    }
}
