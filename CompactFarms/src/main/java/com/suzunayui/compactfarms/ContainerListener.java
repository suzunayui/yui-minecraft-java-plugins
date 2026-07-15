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
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class ContainerListener implements Listener {
    
    private final CompactFarms plugin;
    
    public ContainerListener(CompactFarms plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (!(block.getState() instanceof Container container)) return;
        if (!isCompactFarmsItem(event.getItemInHand()) && !isCompactFarms(container)) return;

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
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getState() instanceof Container container) {
            if (isCompactFarms(container)) {
                var player = event.getPlayer();
                UUID owner = loadOwner(container);
                if (owner != null) {
                    if (!player.getUniqueId().equals(owner)) {
                        String ownerName = getOwnerName(owner);
                        player.sendMessage("§cこのCompactFarmsは" + ownerName + "のものです。破壊できません。");
                        event.setCancelled(true);
                        return;
                    }
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
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getInventory().getHolder() instanceof Container container)) return;
        if (!isCompactFarms(container)) return;
        
        UUID owner = loadOwner(container);
        if (owner == null) return;
        
        UUID playerId = event.getPlayer().getUniqueId();
        if (!playerId.equals(owner)) {
            String ownerName = getOwnerName(owner);
            event.getPlayer().sendMessage("§cこのCompactFarmsは" + ownerName + "のものです。");
            event.setCancelled(true);
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
            NamespacedKey ownerKey = new NamespacedKey(plugin, "owner");
            NamespacedKey cfKey = new NamespacedKey(plugin, "compactfarms");
            pdc.set(ownerKey, PersistentDataType.STRING, owner.toString());
            pdc.set(cfKey, PersistentDataType.BOOLEAN, true);
            tileState.update();
        }
    }
    
    public UUID loadOwner(Container container) {
        if (container instanceof TileState tileState) {
            PersistentDataContainer pdc = tileState.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(plugin, "owner");
            if (pdc.has(key, PersistentDataType.STRING)) {
                String ownerStr = pdc.get(key, PersistentDataType.STRING);
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
            NamespacedKey ownerKey = new NamespacedKey(plugin, "owner");
            NamespacedKey cfKey = new NamespacedKey(plugin, "compactfarms");
            pdc.remove(ownerKey);
            pdc.remove(cfKey);
            tileState.update();
        }
    }
    
    private boolean isCompactFarms(Container container) {
        if (!(container instanceof TileState tileState)) return false;
        PersistentDataContainer pdc = tileState.getPersistentDataContainer();
        NamespacedKey cfKey = new NamespacedKey(plugin, "compactfarms");
        NamespacedKey ownerKey = new NamespacedKey(plugin, "owner");
        return pdc.has(cfKey, PersistentDataType.BOOLEAN)
            || pdc.has(ownerKey, PersistentDataType.STRING);
    }

    private boolean isCompactFarmsItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        NamespacedKey key = new NamespacedKey(plugin, "compactfarms");
        return meta.getPersistentDataContainer().has(key, PersistentDataType.BOOLEAN);
    }
    
    private String getResourceName(Container container) {
        Material type = container.getType();
        if (type == Material.WHITE_SHULKER_BOX) {
            return "鉄";
        } else if (type == Material.GREEN_SHULKER_BOX) {
            return "エメラルド";
        } else if (type == Material.GRAY_SHULKER_BOX) {
            return "火薬";
        } else if (type == Material.LIME_SHULKER_BOX) {
            return "経験値";
        }
        return "";
    }
    
    private String getOwnerName(UUID owner) {
        var player = org.bukkit.Bukkit.getPlayer(owner);
        if (player != null) {
            return player.getName();
        }
        var offlinePlayer = org.bukkit.Bukkit.getOfflinePlayer(owner);
        String name = offlinePlayer.getName();
        return name != null ? name : "不明";
    }
}
