package com.suzunayui.returnhome;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeManager {
    
    private final ReturnHome plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, BukkitRunnable> pendingTeleports = new HashMap<>();
    private final Map<UUID, Location> startLocations = new HashMap<>();
    
    public HomeManager(ReturnHome plugin) {
        this.plugin = plugin;
    }
    
    public void requestHome(Player player) {
        UUID uuid = player.getUniqueId();
        
        if (pendingTeleports.containsKey(uuid)) {
            player.sendMessage(Component.text("既に帰還カウントダウン中です。").color(NamedTextColor.RED));
            return;
        }
        
        if (isOnCooldown(uuid)) {
            long remaining = getCooldownRemaining(uuid);
            int minutes = (int) (remaining / 60);
            int seconds = (int) (remaining % 60);
            player.sendMessage(Component.text("クールタイム中です。残り: " + minutes + "分" + seconds + "秒").color(NamedTextColor.RED));
            return;
        }
        
        Location homeLocation = getHomeLocation(player);
        if (homeLocation == null) {
            player.sendMessage(Component.text("帰還先が見つかりません。").color(NamedTextColor.RED));
            return;
        }
        
        startLocations.put(uuid, player.getLocation().clone());
        
        int countdownSeconds = plugin.getConfig().getInt("countdown-seconds", 10);
        
        player.sendMessage(Component.text("帰還まで" + countdownSeconds + "秒... 動かないでください。").color(NamedTextColor.YELLOW));
        
        BukkitRunnable countdownTask = new BukkitRunnable() {
            int remaining = countdownSeconds;
            
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancelTeleport(uuid);
                    return;
                }
                
                Location currentLoc = player.getLocation();
                Location startLoc = startLocations.get(uuid);
                
                if (startLoc != null && (currentLoc.getBlockX() != startLoc.getBlockX() || 
                    currentLoc.getBlockY() != startLoc.getBlockY() || 
                    currentLoc.getBlockZ() != startLoc.getBlockZ())) {
                    player.sendMessage(Component.text("移動したため、帰還がキャンセルされました。").color(NamedTextColor.RED));
                    cancelTeleport(uuid);
                    return;
                }
                
                remaining--;
                
                if (remaining <= 0) {
                    player.teleport(homeLocation);
                    player.sendMessage(Component.text("帰還しました！").color(NamedTextColor.GREEN));
                    setCooldown(uuid);
                    cleanup(uuid);
                    cancel();
                } else if (remaining <= 3) {
                    player.sendMessage(Component.text("帰還まで" + remaining + "秒...").color(NamedTextColor.YELLOW));
                }
            }
        };
        
        countdownTask.runTaskTimer(plugin, 20L, 20L);
        pendingTeleports.put(uuid, countdownTask);
    }
    
    public void cancelTeleport(UUID uuid) {
        BukkitRunnable task = pendingTeleports.remove(uuid);
        if (task != null) {
            task.cancel();
        }
        startLocations.remove(uuid);
    }
    
    private void cleanup(UUID uuid) {
        pendingTeleports.remove(uuid);
        startLocations.remove(uuid);
    }
    
    private Location getHomeLocation(Player player) {
        Location bedSpawn = player.getBedSpawnLocation();
        if (bedSpawn != null) {
            return bedSpawn;
        }
        return player.getWorld().getSpawnLocation();
    }
    
    private void setCooldown(UUID uuid) {
        int cooldownMinutes = plugin.getConfig().getInt("cooldown-minutes", 10);
        cooldowns.put(uuid, System.currentTimeMillis() + (cooldownMinutes * 60 * 1000L));
    }
    
    public boolean isOnCooldown(UUID uuid) {
        if (!cooldowns.containsKey(uuid)) {
            return false;
        }
        return System.currentTimeMillis() < cooldowns.get(uuid);
    }
    
    public long getCooldownRemaining(UUID uuid) {
        if (!cooldowns.containsKey(uuid)) {
            return 0;
        }
        long remaining = (cooldowns.get(uuid) - System.currentTimeMillis()) / 1000;
        return Math.max(0, remaining);
    }
}
