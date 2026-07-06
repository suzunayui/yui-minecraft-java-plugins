package com.suzunayui.tinyprotect;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

public class InspectListener implements Listener {

    private final TinyProtect plugin;

    public InspectListener(TinyProtect plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        if (!plugin.isInspectMode(player.getUniqueId())) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        event.setCancelled(true);

        Location loc = block.getLocation();
        DatabaseManager db = plugin.getDatabaseManager();

        List<DatabaseManager.LogEntry> entries = db.searchByLocation(
                loc.getWorld().getName(),
                loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(),
                0, 10
        );

        player.sendMessage("§6=== TinyProtect Inspect ===");
        player.sendMessage("§eLocation: " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
        player.sendMessage("§eBlock: §f" + block.getType().name());
        player.sendMessage("");

        if (entries.isEmpty()) {
            player.sendMessage("§7No history found for this block.");
        } else {
            for (DatabaseManager.LogEntry entry : entries) {
                String time = db.formatTimestamp(entry.timestamp);
                String action = formatAction(entry.actionType);
                String detail = "";
                if (entry.blockType != null) detail = " §f" + entry.blockType;
                if (entry.itemType != null) detail = " §f" + entry.itemType + " x" + entry.itemAmount;

                player.sendMessage("§7[" + time + "] §b" + action + " §e" + entry.playerName + detail);
            }
        }
    }

    private String formatAction(String actionType) {
        return switch (actionType) {
            case "BLOCK_BREAK" -> "§c[Broke]";
            case "BLOCK_PLACE" -> "§a[Placed]";
            case "CONTAINER_REMOVE" -> "§c[Took]";
            case "CONTAINER_ADD" -> "§a[Added]";
            case "ITEM_PICKUP" -> "§e[Pickup]";
            case "ITEM_DROP" -> "§7[Drop]";
            case "PLAYER_DEATH" -> "§4[Death]";
            default -> "§8[" + actionType + "]";
        };
    }
}
