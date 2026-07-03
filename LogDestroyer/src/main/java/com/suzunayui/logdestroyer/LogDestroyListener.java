package com.suzunayui.logdestroyer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LogDestroyListener implements Listener {
    
    private final LogDestroyer plugin;
    private static final int MAX_BLOCKS = 64;
    
    public LogDestroyListener(LogDestroyer plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        
        if (!isWoodenAxe(player.getInventory().getItemInMainHand())) {
            return;
        }
        
        if (!isLog(block.getType())) {
            return;
        }
        
        List<Block> blocksToBreak = findConnectedBlocks(block);
        
        if (hasBuildingBlockNearby(blocksToBreak)) {
            player.sendMessage("§c付近に建築ブロックがあるため、一括破壊をキャンセルしました。");
            return;
        }
        
        for (Block b : blocksToBreak) {
            if (!b.equals(block)) {
                b.breakNaturally(player.getInventory().getItemInMainHand());
            }
        }
    }
    
    private boolean hasBuildingBlockNearby(List<Block> blocks) {
        Set<Location> checked = new HashSet<>();
        
        for (Block block : blocks) {
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        Block adjacent = block.getRelative(x, y, z);
                        Location loc = adjacent.getLocation();
                        
                        if (checked.contains(loc)) continue;
                        checked.add(loc);
                        
                        Material type = adjacent.getType();
                        if (!isLog(type) && !isLeaf(type) && !isNaturalBlock(type) && type.isSolid()) {
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    private List<Block> findConnectedBlocks(Block startBlock) {
        List<Block> result = new ArrayList<>();
        Set<Location> visited = new HashSet<>();
        Queue<Block> queue = new LinkedList<>();
        
        queue.add(startBlock);
        visited.add(startBlock.getLocation());
        
        while (!queue.isEmpty() && result.size() < MAX_BLOCKS) {
            Block current = queue.poll();
            result.add(current);
            
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;
                        
                        Block adjacent = current.getRelative(x, y, z);
                        Location loc = adjacent.getLocation();
                        
                        if (visited.contains(loc)) continue;
                        
                        Material type = adjacent.getType();
                        if (isLog(type) || isLeaf(type)) {
                            visited.add(loc);
                            queue.add(adjacent);
                        }
                    }
                }
            }
        }
        
        return result;
    }
    
    private boolean isWoodenAxe(ItemStack item) {
        if (item == null) return false;
        Material type = item.getType();
        return type == Material.WOODEN_AXE;
    }
    
    private boolean isLog(Material material) {
        return material == Material.OAK_LOG
            || material == Material.SPRUCE_LOG
            || material == Material.BIRCH_LOG
            || material == Material.JUNGLE_LOG
            || material == Material.ACACIA_LOG
            || material == Material.DARK_OAK_LOG
            || material == Material.MANGROVE_LOG
            || material == Material.CHERRY_LOG
            || material == Material.OAK_WOOD
            || material == Material.SPRUCE_WOOD
            || material == Material.BIRCH_WOOD
            || material == Material.JUNGLE_WOOD
            || material == Material.ACACIA_WOOD
            || material == Material.DARK_OAK_WOOD
            || material == Material.MANGROVE_WOOD
            || material == Material.CHERRY_WOOD
            || material == Material.STRIPPED_OAK_LOG
            || material == Material.STRIPPED_SPRUCE_LOG
            || material == Material.STRIPPED_BIRCH_LOG
            || material == Material.STRIPPED_JUNGLE_LOG
            || material == Material.STRIPPED_ACACIA_LOG
            || material == Material.STRIPPED_DARK_OAK_LOG
            || material == Material.STRIPPED_MANGROVE_LOG
            || material == Material.STRIPPED_CHERRY_LOG
            || material == Material.STRIPPED_OAK_WOOD
            || material == Material.STRIPPED_SPRUCE_WOOD
            || material == Material.STRIPPED_BIRCH_WOOD
            || material == Material.STRIPPED_JUNGLE_WOOD
            || material == Material.STRIPPED_ACACIA_WOOD
            || material == Material.STRIPPED_DARK_OAK_WOOD
            || material == Material.STRIPPED_MANGROVE_WOOD
            || material == Material.STRIPPED_CHERRY_WOOD;
    }
    
    private boolean isLeaf(Material material) {
        return material == Material.OAK_LEAVES
            || material == Material.SPRUCE_LEAVES
            || material == Material.BIRCH_LEAVES
            || material == Material.JUNGLE_LEAVES
            || material == Material.ACACIA_LEAVES
            || material == Material.DARK_OAK_LEAVES
            || material == Material.MANGROVE_LEAVES
            || material == Material.CHERRY_LEAVES
            || material == Material.AZALEA_LEAVES
            || material == Material.FLOWERING_AZALEA_LEAVES;
    }
    
    private boolean isNaturalBlock(Material material) {
        return material == Material.DIRT
            || material == Material.GRASS_BLOCK
            || material == Material.STONE
            || material == Material.COBBLESTONE
            || material == Material.GRAVEL
            || material == Material.SAND
            || material == Material.RED_SAND
            || material == Material.CLAY
            || material == Material.SNOW
            || material == Material.SNOW_BLOCK
            || material == Material.ICE
            || material == Material.PACKED_ICE
            || material == Material.BLUE_ICE
            || material == Material.WATER
            || material == Material.LAVA
            || material == Material.MOSS_BLOCK
            || material == Material.MOSS_CARPET
            || material == Material.MUD
            || material == Material.SANDSTONE
            || material == Material.RED_SANDSTONE
            || material == Material.DEEPSLATE
            || material == Material.TUFF
            || material == Material.CALCITE
            || material == Material.DRIPSTONE_BLOCK
            || material == Material.POINTED_DRIPSTONE
            || material == Material.AIR
            || material == Material.CAVE_AIR
            || material == Material.VOID_AIR;
    }
}
