package com.suzunayui.containersearch;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ContainerScanner {
    
    private final ContainerSearch plugin;
    
    public ContainerScanner(ContainerSearch plugin) {
        this.plugin = plugin;
    }
    
    public List<SearchResult> search(Player player, Material targetItem) {
        List<SearchResult> results = new ArrayList<>();
        
        int rangeX = plugin.getConfig().getInt("search.range-x", 10);
        int rangeZ = plugin.getConfig().getInt("search.range-z", 10);
        int rangeYMin = plugin.getConfig().getInt("search.range-y-min", -1);
        int rangeYMax = plugin.getConfig().getInt("search.range-y-max", 5);
        
        Location playerLoc = player.getLocation();
        World world = playerLoc.getWorld();
        
        int baseX = playerLoc.getBlockX();
        int baseY = playerLoc.getBlockY();
        int baseZ = playerLoc.getBlockZ();
        
        for (int x = baseX - rangeX; x <= baseX + rangeX; x++) {
            for (int z = baseZ - rangeZ; z <= baseZ + rangeZ; z++) {
                for (int y = baseY + rangeYMin; y <= baseY + rangeYMax; y++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getState() instanceof Container container) {
                        searchContainer(container, targetItem, results, new ArrayList<>(), player);
                    }
                }
            }
        }
        
        return results;
    }
    
    private void searchContainer(Container container, Material targetItem, List<SearchResult> results, List<String> path, Player player) {
        Inventory inventory = container.getInventory();
        
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null) continue;
            
            List<String> currentPath = new ArrayList<>(path);
            if (currentPath.isEmpty()) {
                currentPath.add(getContainerName(container));
            }
            
            if (item.getType() == targetItem) {
                results.add(new SearchResult(
                    container.getLocation(),
                    item.getType(),
                    item.getAmount(),
                    new ArrayList<>(currentPath)
                ));
            }
            
            if (isShulkerBox(item.getType())) {
                currentPath.add(item.getType().name());
                searchShulkerBox(item, targetItem, results, currentPath, container.getLocation(), player);
            }
        }
    }
    
    private void searchShulkerBox(ItemStack shulkerItem, Material targetItem, List<SearchResult> results, List<String> path, Location location, Player player) {
        Inventory shulkerInv = org.bukkit.inventory.ItemStack.class.cast(shulkerItem).getItemMeta() instanceof org.bukkit.inventory.meta.BlockStateMeta bsm
            && bsm.hasBlockState()
            && bsm.getBlockState() instanceof ShulkerBox shulker
            ? shulker.getInventory()
            : null;
        
        if (shulkerInv == null) return;
        
        for (int i = 0; i < shulkerInv.getSize(); i++) {
            ItemStack item = shulkerInv.getItem(i);
            if (item == null) continue;
            
            if (item.getType() == targetItem) {
                results.add(new SearchResult(
                    location,
                    item.getType(),
                    item.getAmount(),
                    new ArrayList<>(path)
                ));
            }
            
            if (isShulkerBox(item.getType())) {
                List<String> newPath = new ArrayList<>(path);
                newPath.add(item.getType().name());
                searchShulkerBox(item, targetItem, results, newPath, location, player);
            }
        }
    }
    
    private boolean isShulkerBox(Material material) {
        return material == Material.SHULKER_BOX
            || material == Material.WHITE_SHULKER_BOX
            || material == Material.ORANGE_SHULKER_BOX
            || material == Material.MAGENTA_SHULKER_BOX
            || material == Material.LIGHT_BLUE_SHULKER_BOX
            || material == Material.YELLOW_SHULKER_BOX
            || material == Material.LIME_SHULKER_BOX
            || material == Material.PINK_SHULKER_BOX
            || material == Material.GRAY_SHULKER_BOX
            || material == Material.LIGHT_GRAY_SHULKER_BOX
            || material == Material.CYAN_SHULKER_BOX
            || material == Material.PURPLE_SHULKER_BOX
            || material == Material.BLUE_SHULKER_BOX
            || material == Material.BROWN_SHULKER_BOX
            || material == Material.GREEN_SHULKER_BOX
            || material == Material.RED_SHULKER_BOX
            || material == Material.BLACK_SHULKER_BOX;
    }
    
    private String getContainerName(Container container) {
        Material type = container.getType();
        return switch (type) {
            case CHEST, TRAPPED_CHEST -> "チェスト";
            case BARREL -> "タル";
            case DISPENSER -> "ディスペンサー";
            case DROPPER -> "ドロッパー";
            case FURNACE -> "かまど";
            case BLAST_FURNACE -> "溶鉱炉";
            case SMOKER -> "燻製器";
            case HOPPER -> "ホッパー";
            case BREWING_STAND -> "醸造台";
            case LECTERN -> "書見台";
            default -> {
                if (type.name().contains("SHULKER_BOX")) {
                    yield "シュルカーボックス";
                }
                yield type.name();
            }
        };
    }
    
    public static class SearchResult {
        private final Location location;
        private final Material item;
        private final int amount;
        private final List<String> path;
        
        public SearchResult(Location location, Material item, int amount, List<String> path) {
            this.location = location;
            this.item = item;
            this.amount = amount;
            this.path = path;
        }
        
        public Location getLocation() {
            return location;
        }
        
        public Material getItem() {
            return item;
        }
        
        public int getAmount() {
            return amount;
        }
        
        public List<String> getPath() {
            return path;
        }
    }
}
