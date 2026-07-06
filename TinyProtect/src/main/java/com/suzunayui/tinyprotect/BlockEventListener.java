package com.suzunayui.tinyprotect;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        String cause = event.getEntity() != null ? event.getEntity().getType().name() : "UNKNOWN";
        for (Block block : event.blockList()) {
            Location loc = block.getLocation();
            Material blockType = block.getType();
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
    public void onBlockFromTo(BlockFromToEvent event) {
        Block toBlock = event.getToBlock();
        Material toType = toBlock.getType();
        
        if (toType == Material.AIR || toType == Material.CAVE_AIR || toType == Material.VOID_AIR) {
            return;
        }
        
        Material liquidType = event.getBlock().getType();
        if (liquidType != Material.WATER && liquidType != Material.LAVA) {
            return;
        }
        
        Location loc = toBlock.getLocation();
        plugin.getDatabaseManager().logLiquidDestroy(loc, toType, liquidType);
    }
}
