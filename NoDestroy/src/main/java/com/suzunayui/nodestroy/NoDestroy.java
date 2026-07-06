package com.suzunayui.nodestroy;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NoDestroy extends JavaPlugin implements Listener {

    private final Set<UUID> allowedPlayers = new HashSet<>();
    private boolean fireSpreadAllowed = true;

    @Override
    public void onEnable() {
        loadConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("nd").setExecutor(new NoDestroyCommand(this));
        getCommand("nd").setTabCompleter(new NoDestroyCommand(this));
        getLogger().info("NoDestroy has been enabled!");
    }

    @Override
    public void onDisable() {
        saveConfig();
        getLogger().info("NoDestroy has been disabled!");
    }

    public boolean isAllowed(UUID playerId) {
        return allowedPlayers.contains(playerId);
    }

    public void addAllowedPlayer(UUID playerId) {
        allowedPlayers.add(playerId);
        saveConfig();
    }

    public void removeAllowedPlayer(UUID playerId) {
        allowedPlayers.remove(playerId);
        saveConfig();
    }

    public Set<UUID> getAllowedPlayers() {
        return allowedPlayers;
    }

    public boolean isFireSpreadAllowed() {
        return fireSpreadAllowed;
    }

    public void setFireSpreadAllowed(boolean allowed) {
        this.fireSpreadAllowed = allowed;
        saveConfig();
    }

    private void loadConfig() {
        FileConfiguration config = getConfig();
        
        if (config.contains("allowed-players")) {
            for (String uuidStr : config.getStringList("allowed-players")) {
                try {
                    allowedPlayers.add(UUID.fromString(uuidStr));
                } catch (IllegalArgumentException e) {
                    getLogger().warning("Invalid UUID in config: " + uuidStr);
                }
            }
        }
        
        fireSpreadAllowed = config.getBoolean("fire-spread-allowed", true);
        
        getLogger().info("Loaded " + allowedPlayers.size() + " allowed players.");
        getLogger().info("Fire spread: " + (fireSpreadAllowed ? "enabled" : "disabled"));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.TNT) {
            Player player = event.getPlayer();
            if (!isAllowed(player.getUniqueId())) {
                player.sendMessage("§cTNTの設置は禁止されています。");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.LAVA_BUCKET) {
            if (!isAllowed(player.getUniqueId())) {
                player.sendMessage("§c溶岩バケツの使用は禁止されています。");
                event.setCancelled(true);
            }
            return;
        }

        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.TNT) {
            if (item.getType() == Material.FLINT_AND_STEEL || item.getType() == Material.FIRE_CHARGE) {
                if (!isAllowed(player.getUniqueId())) {
                    player.sendMessage("§cTNTの点火は禁止されています。");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        if (!fireSpreadAllowed) {
            event.setCancelled(true);
        }
    }
}
