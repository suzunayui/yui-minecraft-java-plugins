package com.suzunayui.tinyprotect;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerEventListener implements Listener {

    private final TinyProtect plugin;

    public PlayerEventListener(TinyProtect plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        String killerName = "N/A";
        String cause = event.getEntity().getLastDamageCause() != null
                ? event.getEntity().getLastDamageCause().getCause().name()
                : "UNKNOWN";

        Entity killer = player.getKiller();
        if (killer instanceof Player killerPlayer) {
            killerName = killerPlayer.getName();
            plugin.getDatabaseManager().logPlayerKill(
                    killerPlayer.getUniqueId(),
                    killerPlayer.getName(),
                    player.getLocation(),
                    player.getName(),
                    cause
            );
        }

        plugin.getDatabaseManager().logPlayerKill(
                player.getUniqueId(),
                player.getName(),
                player.getLocation(),
                killerName,
                cause
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("tinyprotect.bypass")) return;

        plugin.getDatabaseManager().logItemDrop(
                player.getUniqueId(),
                player.getName(),
                event.getItemDrop().getLocation(),
                event.getItemDrop().getItemStack()
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.hasPermission("tinyprotect.bypass")) return;

        plugin.getDatabaseManager().logItemPickup(
                player.getUniqueId(),
                player.getName(),
                player.getLocation(),
                event.getItem().getItemStack()
        );
    }
}
