package com.suzunayui.tinyprotect;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.Set;
import java.util.UUID;

public class PlayerEventListener implements Listener {

    private final TinyProtect plugin;
    private static final Set<EntityType> TRACKED_ENTITIES = Set.of(
            EntityType.VILLAGER,
            EntityType.COW,
            EntityType.PIG,
            EntityType.CHICKEN,
            EntityType.SHEEP,
            EntityType.HORSE,
            EntityType.DONKEY,
            EntityType.MULE,
            EntityType.LLAMA,
            EntityType.RABBIT,
            EntityType.WOLF,
            EntityType.CAT,
            EntityType.PARROT,
            EntityType.TURTLE,
            EntityType.FOX,
            EntityType.BEE,
            EntityType.STRIDER,
            EntityType.HOGLIN,
            EntityType.PIGLIN,
            EntityType.GOAT,
            EntityType.AXOLOTL
    );

    public PlayerEventListener(TinyProtect plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        String cause = entity.getLastDamageCause() != null
                ? entity.getLastDamageCause().getCause().name()
                : "UNKNOWN";

        if (entity instanceof Player player) {
            String killerName = "N/A";
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
        } else if (TRACKED_ENTITIES.contains(entity.getType())) {
            if (entity instanceof LivingEntity livingEntity) {
                Entity killer = livingEntity.getKiller();
                if (killer instanceof Player killerPlayer) {
                    plugin.getDatabaseManager().logEntityKill(
                            killerPlayer.getUniqueId(),
                            killerPlayer.getName(),
                            entity.getLocation(),
                            entity.getType().name(),
                            cause
                    );
                }
            }
        }
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
