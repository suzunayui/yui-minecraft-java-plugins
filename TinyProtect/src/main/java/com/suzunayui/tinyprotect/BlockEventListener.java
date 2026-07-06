package com.suzunayui.tinyprotect;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

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
}
