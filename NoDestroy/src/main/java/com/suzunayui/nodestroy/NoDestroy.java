package com.suzunayui.nodestroy;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.block.TileState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NoDestroy extends JavaPlugin implements Listener {

    private final Set<UUID> allowedPlayers = new HashSet<>();
    private boolean fireSpreadAllowed = true;
    private boolean lavaDestroyAllowed = true;

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

    public boolean isLavaDestroyAllowed() {
        return lavaDestroyAllowed;
    }

    public void setLavaDestroyAllowed(boolean allowed) {
        this.lavaDestroyAllowed = allowed;
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
        lavaDestroyAllowed = config.getBoolean("lava-destroy-allowed", true);
        
        getLogger().info("Loaded " + allowedPlayers.size() + " allowed players.");
        getLogger().info("Fire spread: " + (fireSpreadAllowed ? "enabled" : "disabled"));
        getLogger().info("Lava destroy: " + (lavaDestroyAllowed ? "enabled" : "disabled"));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        
        // TNTの設置禁止
        if (block.getType() == Material.TNT) {
            if (!isAllowed(player.getUniqueId())) {
                player.sendMessage("§cTNTの設置は禁止されています。");
                event.setCancelled(true);
            }
        }
        
        // ディスペンサーの設置者を記録
        if (block.getType() == Material.DISPENSER) {
            if (block.getState() instanceof TileState tileState) {
                PersistentDataContainer pdc = tileState.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey(this, "dispenser-owner");
                pdc.set(key, PersistentDataType.STRING, player.getUniqueId().toString());
                tileState.update();
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

        // TNT付きトロッコの設置禁止
        if (item.getType() == Material.TNT_MINECART) {
            if (!isAllowed(player.getUniqueId())) {
                player.sendMessage("§cTNT付きトロッコの設置は禁止されています。");
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

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        if (!lavaDestroyAllowed) {
            Material fromType = event.getBlock().getType();
            if (fromType == Material.LAVA) {
                Material toType = event.getToBlock().getType();
                // 溶岩がブロックを破壊する場合（移動先が空気でない場合）
                if (toType != Material.AIR && toType != Material.CAVE_AIR && toType != Material.VOID_AIR && toType != Material.LAVA) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockDispense(BlockDispenseEvent event) {
        ItemStack item = event.getItem();
        Material type = item.getType();
        
        // 溶岩バケツまたはTNT付きトロッコの場合
        if (type == Material.LAVA_BUCKET || type == Material.TNT_MINECART) {
            Block block = event.getBlock();
            
            // ディスペンサーの設置者を取得
            if (block.getState() instanceof TileState tileState) {
                PersistentDataContainer pdc = tileState.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey(this, "dispenser-owner");
                
                if (pdc.has(key, PersistentDataType.STRING)) {
                    String ownerUuid = pdc.get(key, PersistentDataType.STRING);
                    if (ownerUuid != null) {
                        try {
                            UUID ownerId = UUID.fromString(ownerUuid);
                            // 設置者が許可リストに含まれている場合のみ射出を許可
                            if (isAllowed(ownerId)) {
                                return;
                            }
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                }
            }
            
            // 許可されていない場合
            event.setCancelled(true);
        }
    }
}
