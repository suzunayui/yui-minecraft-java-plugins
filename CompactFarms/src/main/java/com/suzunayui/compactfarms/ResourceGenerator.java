package com.suzunayui.compactfarms;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ResourceGenerator {
    
    private static ResourceGenerator instance;
    private final CompactFarms plugin;
    private final Map<UUID, List<Location>> registeredContainers;
    private BukkitRunnable task;
    
    public ResourceGenerator(CompactFarms plugin) {
        this.plugin = plugin;
        this.registeredContainers = new HashMap<>();
    }
    
    public static ResourceGenerator getInstance(CompactFarms plugin) {
        if (instance == null) {
            instance = new ResourceGenerator(plugin);
        }
        return instance;
    }
    
    public static ResourceGenerator getInstance() {
        return instance;
    }
    
    public void start() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                generateResources();
            }
        };
        
        task.runTaskTimer(plugin, 1200L, 1200L);
    }
    
    public void stop() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }
    
    private void generateResources() {
        FileConfiguration config = plugin.getConfig();
        
        for (Map.Entry<UUID, List<Location>> entry : registeredContainers.entrySet()) {
            List<Location> locations = entry.getValue();
            locations.removeIf(loc -> loc.getWorld() == null);
            
            for (Location loc : locations) {
                if (!loc.getChunk().isLoaded()) continue;
                
                Block block = loc.getBlock();
                if (!(block.getState() instanceof Container container)) continue;
                
                if (container.getInventory().getViewers().isEmpty()) {
                    Material resourceType = getResourceType(container);
                    if (resourceType == null) continue;
                    
                    int amount = config.getInt("resources." + resourceType.name() + ".amount", 1);
                    
                    ItemStack item = new ItemStack(resourceType, amount);
                    
                    if (container.getInventory().firstEmpty() != -1) {
                        container.getInventory().addItem(item);
                    }
                }
            }
        }
    }
    
    private Material getResourceType(Container container) {
        Material containerType = container.getType();
        
        if (containerType == Material.WHITE_SHULKER_BOX) {
            return Material.IRON_INGOT;
        } else if (containerType == Material.GREEN_SHULKER_BOX) {
            return Material.EMERALD;
        } else if (containerType == Material.GRAY_SHULKER_BOX) {
            return Material.GUNPOWDER;
        } else if (containerType == Material.LIME_SHULKER_BOX) {
            return Material.EXPERIENCE_BOTTLE;
        }
        
        return null;
    }
    
    public void registerContainer(UUID owner, Location location) {
        if (isLocationRegistered(owner, location)) return;
        registeredContainers.computeIfAbsent(owner, k -> new ArrayList<>()).add(location);
    }
    
    public void unregisterContainer(UUID owner, Location location) {
        List<Location> locations = registeredContainers.get(owner);
        if (locations != null) {
            locations.removeIf(loc ->
                loc.getWorld() == location.getWorld() &&
                loc.getBlockX() == location.getBlockX() &&
                loc.getBlockY() == location.getBlockY() &&
                loc.getBlockZ() == location.getBlockZ()
            );
        }
    }
    
    public boolean isLocationRegistered(UUID owner, Location location) {
        List<Location> locations = registeredContainers.get(owner);
        if (locations == null) return false;
        for (Location loc : locations) {
            if (loc.getWorld() == location.getWorld() &&
                loc.getBlockX() == location.getBlockX() &&
                loc.getBlockY() == location.getBlockY() &&
                loc.getBlockZ() == location.getBlockZ()) {
                return true;
            }
        }
        return false;
    }
    
    public int getContainerCount(UUID owner) {
        List<Location> locations = registeredContainers.get(owner);
        return locations != null ? locations.size() : 0;
    }
    
    public int getContainerCountOfType(UUID owner, Material containerType) {
        List<Location> locations = registeredContainers.get(owner);
        if (locations == null) return 0;
        int count = 0;
        for (Location loc : locations) {
            if (loc.getWorld() == null) continue;
            if (!loc.getChunk().isLoaded()) continue;
            Block block = loc.getBlock();
            if (block.getType() == containerType) {
                count++;
            }
        }
        return count;
    }
}
