package com.suzunayui.compactfarms;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.UUID;

public class ContainerListener implements Listener {
    
    private final CompactFarms plugin;
    
    public ContainerListener(CompactFarms plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getState() instanceof Container container) {
            if (isCompactFarms(container)) {
                var player = event.getPlayer();
                UUID owner = player.getUniqueId();
                
                int maxPerType = plugin.getConfig().getInt("global.max-per-type", 1);
                int currentCount = ResourceGenerator.getInstance().getContainerCountOfType(owner, container.getType());
                if (currentCount >= maxPerType) {
                    String resourceName = getResourceName(container);
                    player.sendMessage("§c" + resourceName + "CompactFarmsは1人" + maxPerType + "個までです。");
                    event.setCancelled(true);
                    return;
                }
                
                saveOwner(container, owner);
                Location loc = new Location(block.getWorld(), block.getX(), block.getY(), block.getZ());
                ResourceGenerator.getInstance().registerContainer(owner, loc);
                String resourceName = getResourceName(container);
                player.sendMessage("§a" + resourceName + "CompactFarmsを登録しました！自動的に資源が生成されます。");
            }
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getState() instanceof Container container) {
            if (isCompactFarms(container)) {
                var player = event.getPlayer();
                UUID owner = loadOwner(container);
                if (owner != null) {
                    Location loc = new Location(block.getWorld(), block.getX(), block.getY(), block.getZ());
                    ResourceGenerator.getInstance().unregisterContainer(owner, loc);
                }
                clearOwner(container);
                String resourceName = getResourceName(container);
                player.sendMessage("§e" + resourceName + "CompactFarmsを解除しました。");
            }
        }
    }
    
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (org.bukkit.block.BlockState blockState : event.getChunk().getTileEntities()) {
            if (blockState instanceof Container container) {
                if (isCompactFarms(container)) {
                    UUID owner = loadOwner(container);
                    if (owner != null) {
                        Location loc = new Location(blockState.getWorld(), blockState.getX(), blockState.getY(), blockState.getZ());
                        ResourceGenerator.getInstance().registerContainer(owner, loc);
                    }
                }
            }
        }
    }
    
    public void saveOwner(Container container, UUID owner) {
        if (container instanceof TileState tileState) {
            PersistentDataContainer pdc = tileState.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(plugin, "owner");
            pdc.set(key, org.bukkit.persistence.PersistentDataType.STRING, owner.toString());
            tileState.update();
        }
    }
    
    public UUID loadOwner(Container container) {
        if (container instanceof TileState tileState) {
            PersistentDataContainer pdc = tileState.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(plugin, "owner");
            if (pdc.has(key, org.bukkit.persistence.PersistentDataType.STRING)) {
                String ownerStr = pdc.get(key, org.bukkit.persistence.PersistentDataType.STRING);
                if (ownerStr != null) {
                    return UUID.fromString(ownerStr);
                }
            }
        }
        return null;
    }
    
    public void clearOwner(Container container) {
        if (container instanceof TileState tileState) {
            PersistentDataContainer pdc = tileState.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(plugin, "owner");
            pdc.remove(key);
            tileState.update();
        }
    }
    
    private boolean isCompactFarms(Container container) {
        Material type = container.getType();
        return type == Material.WHITE_SHULKER_BOX || type == Material.GREEN_SHULKER_BOX || type == Material.GRAY_SHULKER_BOX;
    }
    
    private String getResourceName(Container container) {
        Material type = container.getType();
        if (type == Material.WHITE_SHULKER_BOX) {
            return "鉄";
        } else if (type == Material.GREEN_SHULKER_BOX) {
            return "エメラルド";
        } else if (type == Material.GRAY_SHULKER_BOX) {
            return "火薬";
        }
        return "";
    }
}
