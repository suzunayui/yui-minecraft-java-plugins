package com.suzunayui.recyclegacha;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class GachaListener implements Listener {
    
    private final RecycleGacha plugin;
    
    public GachaListener(RecycleGacha plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.HOPPER) {
            Block below = block.getRelative(0, -1, 0);
            if (below.getType() == Material.CHEST) {
                Player player = event.getPlayer();
                if (player.getInventory().getItemInMainHand().getType() == Material.FISHING_ROD) {
                    Hopper hopper = (Hopper) block.getState();
                    plugin.getGachaManager().registerGacha(player, hopper);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.HOPPER) {
            Hopper hopper = (Hopper) block.getState();
            if (plugin.getGachaManager().isGachaHopper(hopper)) {
                plugin.getGachaManager().unregisterGacha(hopper);
                Component message = LegacyComponentSerializer.legacySection().deserialize("§eガチャ機を解除しました。");
                event.getPlayer().sendMessage(message);
            }
        }
    }
    
    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (event.getSource().getHolder() instanceof Hopper hopper) {
            if (plugin.getGachaManager().isGachaHopper(hopper)) {
                ItemStack item = event.getItem();
                int points = plugin.getGachaManager().getItemPoints(item);
                
                if (points > 0) {
                    plugin.getGachaManager().addPoints(hopper, points * item.getAmount());
                    
                    // オーナープレイヤーを取得
                    UUID ownerUUID = plugin.getGachaManager().getOwner(hopper);
                    Player player = ownerUUID != null ? Bukkit.getPlayer(ownerUUID) : null;
                    
                    if (player != null && plugin.getGachaManager().tryGacha(hopper, player)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
