package com.suzunayui.tinyprotect;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;

public class BlockEventListener implements Listener {

    private final TinyProtect plugin;

    public BlockEventListener(TinyProtect plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("tinyprotect.bypass")) return;

        plugin.getDatabaseManager().logBlockBreak(
                player.getUniqueId(),
                player.getName(),
                event.getBlock().getLocation(),
                event.getBlock().getType()
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("tinyprotect.bypass")) return;

        plugin.getDatabaseManager().logBlockPlace(
                player.getUniqueId(),
                player.getName(),
                event.getBlock().getLocation(),
                event.getBlock().getType()
        );
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        String cause = event.getEntity() != null ? event.getEntity().getType().name() : "UNKNOWN";
        for (Block block : event.blockList()) {
            Location loc = block.getLocation();
            Material blockType = block.getType();
            if (blockType == Material.AIR) continue;
            plugin.getDatabaseManager().logExplosion(loc, blockType, cause);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        String cause = event.getBlock() != null ? event.getBlock().getType().name() : "UNKNOWN";
        for (Block block : event.blockList()) {
            Location loc = block.getLocation();
            Material blockType = block.getType();
            if (blockType == Material.AIR) continue;
            plugin.getDatabaseManager().logExplosion(loc, blockType, cause);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();
        Material blockType = block.getType();
        String cause = event.getIgnitingBlock() != null 
                ? event.getIgnitingBlock().getType().name() 
                : "FIRE";
        plugin.getDatabaseManager().logBlockBurn(loc, blockType, cause);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockFade(BlockFadeEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();
        Material oldType = block.getType();
        Material newType = event.getNewState().getType();
        
        // 火が消える場合や、氷が溶ける場合などは記録しない
        if (oldType == Material.FIRE || oldType == Material.SOUL_FIRE) {
            return;
        }
        
        // ブロックが溶岩や火によって消される場合
        if (newType == Material.AIR || newType == Material.FIRE) {
            plugin.getDatabaseManager().logBlockBurn(loc, oldType, "FADE");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        Block fromBlock = event.getBlock();
        Block toBlock = event.getToBlock();
        Material fromType = fromBlock.getType();
        Material toType = toBlock.getType();
        
        // 溶岩や水がブロックを置き換える場合
        if (fromType == Material.LAVA || fromType == Material.WATER) {
            if (toType != Material.AIR && toType != Material.CAVE_AIR && toType != Material.VOID_AIR) {
                // 元のブロックが壊れる場合
                Location loc = toBlock.getLocation();
                plugin.getDatabaseManager().logLiquidDestroy(loc, toType, fromType);
            }
        }
    }
}
