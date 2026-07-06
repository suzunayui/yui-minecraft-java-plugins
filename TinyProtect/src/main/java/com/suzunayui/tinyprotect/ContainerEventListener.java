package com.suzunayui.tinyprotect;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ContainerEventListener implements Listener {

    private final TinyProtect plugin;
    private final Map<String, Map<Integer, ItemStackSnapshot>> openInventories = new HashMap<>();

    public ContainerEventListener(TinyProtect plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (player.hasPermission("tinyprotect.bypass")) return;

        Inventory inv = event.getInventory();
        if (!isContainerInventory(inv)) return;

        Location loc = getContainerLocation(event.getView().getTitle(), player);
        if (loc == null) return;

        String key = makeKey(player.getUniqueId(), loc);
        Map<Integer, ItemStackSnapshot> snapshot = snapshotInventory(inv);
        openInventories.put(key, snapshot);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (player.hasPermission("tinyprotect.bypass")) return;

        Inventory inv = event.getInventory();
        if (!isContainerInventory(inv)) return;

        Location loc = getContainerLocation(event.getView().getTitle(), player);
        if (loc == null) return;

        String key = makeKey(player.getUniqueId(), loc);
        Map<Integer, ItemStackSnapshot> before = openInventories.remove(key);
        if (before == null) return;

        Map<Integer, ItemStackSnapshot> after = snapshotInventory(inv);
        Material containerType = getContainerMaterial(player);

        for (int i = 0; i < inv.getSize(); i++) {
            ItemStackSnapshot beforeItem = before.get(i);
            ItemStackSnapshot afterItem = after.get(i);

            if (beforeItem == null && afterItem == null) continue;

            int beforeAmount = beforeItem != null ? beforeItem.amount : 0;
            int afterAmount = afterItem != null ? afterItem.amount : 0;
            String beforeType = beforeItem != null ? beforeItem.type : null;
            String afterType = afterItem != null ? afterItem.type : null;

            if (afterAmount < beforeAmount) {
                int diff = beforeAmount - afterAmount;
                ItemStack removed = new ItemStack(Material.valueOf(beforeType), diff);
                plugin.getDatabaseManager().logContainerRemove(
                        player.getUniqueId(), player.getName(), loc, containerType, removed
                );
            } else if (afterAmount > beforeAmount) {
                int diff = afterAmount - beforeAmount;
                ItemStack added = new ItemStack(Material.valueOf(afterType), diff);
                plugin.getDatabaseManager().logContainerAdd(
                        player.getUniqueId(), player.getName(), loc, containerType, added
                );
            } else if (!Objects.equals(beforeType, afterType) && beforeType != null && afterType != null) {
                ItemStack removed = new ItemStack(Material.valueOf(beforeType), beforeAmount);
                plugin.getDatabaseManager().logContainerRemove(
                        player.getUniqueId(), player.getName(), loc, containerType, removed
                );
                ItemStack added = new ItemStack(Material.valueOf(afterType), afterAmount);
                plugin.getDatabaseManager().logContainerAdd(
                        player.getUniqueId(), player.getName(), loc, containerType, added
                );
            }
        }
    }

    private boolean isContainerInventory(Inventory inv) {
        return switch (inv.getType()) {
            case CHEST, DISPENSER, DROPPER, FURNACE, BLAST_FURNACE, SMOKER,
                 BREWING, HOPPER, SHULKER_BOX, BARREL ->
                    true;
            default -> false;
        };
    }

    private Location getContainerLocation(String title, Player player) {
        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock != null && isContainerBlock(targetBlock.getType())) {
            return targetBlock.getLocation();
        }
        return null;
    }

    private Material getContainerMaterial(Player player) {
        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock != null && isContainerBlock(targetBlock.getType())) {
            return targetBlock.getType();
        }
        return Material.CHEST;
    }

    private boolean isContainerBlock(Material material) {
        return switch (material) {
            case CHEST, TRAPPED_CHEST, BARREL, DISPENSER, DROPPER, FURNACE,
                 BLAST_FURNACE, SMOKER, BREWING_STAND, HOPPER,
                 SHULKER_BOX, WHITE_SHULKER_BOX, ORANGE_SHULKER_BOX,
                 MAGENTA_SHULKER_BOX, LIGHT_BLUE_SHULKER_BOX, YELLOW_SHULKER_BOX,
                 LIME_SHULKER_BOX, PINK_SHULKER_BOX, GRAY_SHULKER_BOX,
                 LIGHT_GRAY_SHULKER_BOX, CYAN_SHULKER_BOX, PURPLE_SHULKER_BOX,
                 BLUE_SHULKER_BOX, BROWN_SHULKER_BOX, GREEN_SHULKER_BOX,
                 RED_SHULKER_BOX, BLACK_SHULKER_BOX ->
                    true;
            default -> false;
        };
    }

    private Map<Integer, ItemStackSnapshot> snapshotInventory(Inventory inv) {
        Map<Integer, ItemStackSnapshot> snapshot = new HashMap<>();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                snapshot.put(i, new ItemStackSnapshot(item.getType().name(), item.getAmount()));
            }
        }
        return snapshot;
    }

    private String makeKey(UUID playerId, Location loc) {
        return playerId.toString() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
    }

    private record ItemStackSnapshot(String type, int amount) {}
}
