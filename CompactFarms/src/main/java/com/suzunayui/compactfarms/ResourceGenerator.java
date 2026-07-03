package com.suzunayui.compactfarms;

import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ResourceGenerator {
    
    private static ResourceGenerator instance;
    private final CompactFarms plugin;
    private final Map<UUID, List<Container>> registeredContainers;
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
        
        for (List<Container> containers : registeredContainers.values()) {
            for (Container container : containers) {
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
        }
        
        return null;
    }
    
    public void registerContainer(UUID owner, Container container) {
        registeredContainers.computeIfAbsent(owner, k -> new ArrayList<>()).add(container);
    }
    
    public void unregisterContainer(UUID owner, Container container) {
        List<Container> containers = registeredContainers.get(owner);
        if (containers != null) {
            containers.remove(container);
        }
    }
    
    public int getContainerCount(UUID owner) {
        List<Container> containers = registeredContainers.get(owner);
        return containers != null ? containers.size() : 0;
    }
    
    public boolean hasContainerOfType(UUID owner, Material containerType) {
        List<Container> containers = registeredContainers.get(owner);
        if (containers == null) return false;
        return containers.stream().anyMatch(c -> c.getType() == containerType);
    }
    
    public int getContainerCountOfType(UUID owner, Material containerType) {
        List<Container> containers = registeredContainers.get(owner);
        if (containers == null) return 0;
        return (int) containers.stream().filter(c -> c.getType() == containerType).count();
    }
}
