package com.suzunayui.compactfarms;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Container;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.UUID;

public class CompactFarms extends JavaPlugin {
    
    private static CompactFarms instance;
    
    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        
        registerRecipes();
        
        ContainerListener listener = new ContainerListener(this);
        getServer().getPluginManager().registerEvents(listener, this);
        
        if (getCommand("compactfarms") != null) {
            getCommand("compactfarms").setExecutor(new CompactFarmsCommand(this));
        }
        
        ResourceGenerator.getInstance(this).start();
        
        Bukkit.getScheduler().runTaskLater(this, () -> {
            scanAndRegisterExistingContainers(listener);
        }, 100L);
        
        getLogger().info("CompactFarms has been enabled!");
    }
    
    private void scanAndRegisterExistingContainers(ContainerListener listener) {
        int count = 0;
        
        for (org.bukkit.World world : Bukkit.getWorlds()) {
            for (org.bukkit.Chunk chunk : world.getLoadedChunks()) {
                for (org.bukkit.block.BlockState blockState : chunk.getTileEntities()) {
                    if (blockState instanceof Container container) {
                        if (isCompactFarms(container)) {
                            UUID owner = listener.loadOwner(container);
                            if (owner != null) {
                                org.bukkit.Location loc = new org.bukkit.Location(blockState.getWorld(), blockState.getX(), blockState.getY(), blockState.getZ());
                                ResourceGenerator.getInstance().registerContainer(owner, loc);
                                count++;
                            }
                        }
                    }
                }
            }
        }
        
        getLogger().info("Scanned and registered " + count + " existing CompactFarms containers.");
    }
    
    boolean isCompactFarms(org.bukkit.block.Container container) {
        if (!(container instanceof org.bukkit.block.TileState tileState)) return false;
        org.bukkit.persistence.PersistentDataContainer pdc = tileState.getPersistentDataContainer();
        NamespacedKey cfKey = new NamespacedKey(this, "compactfarms");
        NamespacedKey ownerKey = new NamespacedKey(this, "owner");
        return pdc.has(cfKey, PersistentDataType.BOOLEAN)
            || pdc.has(ownerKey, PersistentDataType.STRING);
    }
    
    private void registerRecipes() {
        NamespacedKey ironKey = new NamespacedKey(this, "iron_compactfarms");
        ShapedRecipe ironRecipe = new ShapedRecipe(ironKey, createCompactFarmsItem(Material.WHITE_SHULKER_BOX, "iron"));
        ironRecipe.shape("III", "ICI", "III");
        ironRecipe.setIngredient('I', Material.IRON_INGOT);
        ironRecipe.setIngredient('C', Material.CHEST);
        Bukkit.addRecipe(ironRecipe);
        
        NamespacedKey emeraldKey = new NamespacedKey(this, "emerald_compactfarms");
        ShapedRecipe emeraldRecipe = new ShapedRecipe(emeraldKey, createCompactFarmsItem(Material.GREEN_SHULKER_BOX, "emerald"));
        emeraldRecipe.shape("EEE", "ECE", "EEE");
        emeraldRecipe.setIngredient('E', Material.EMERALD);
        emeraldRecipe.setIngredient('C', Material.CHEST);
        Bukkit.addRecipe(emeraldRecipe);
        
        NamespacedKey gunpowderKey = new NamespacedKey(this, "gunpowder_compactfarms");
        ShapedRecipe gunpowderRecipe = new ShapedRecipe(gunpowderKey, createCompactFarmsItem(Material.GRAY_SHULKER_BOX, "gunpowder"));
        gunpowderRecipe.shape("GGG", "GCG", "GGG");
        gunpowderRecipe.setIngredient('G', Material.GUNPOWDER);
        gunpowderRecipe.setIngredient('C', Material.CHEST);
        Bukkit.addRecipe(gunpowderRecipe);
        
        NamespacedKey expKey = new NamespacedKey(this, "experience_compactfarms");
        ShapedRecipe expRecipe = new ShapedRecipe(expKey, createCompactFarmsItem(Material.LIME_SHULKER_BOX, "experience"));
        expRecipe.shape("XXX", "XCX", "XXX");
        expRecipe.setIngredient('X', Material.EXPERIENCE_BOTTLE);
        expRecipe.setIngredient('C', Material.CHEST);
        Bukkit.addRecipe(expRecipe);
        
        getLogger().info("Registered CompactFarms recipes");
    }
    
    private ItemStack createCompactFarmsItem(Material material, String type) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (type.equals("iron")) {
                meta.displayName(Component.text("鉄CompactFarms").color(NamedTextColor.BLACK));
                meta.lore(List.of(
                    Component.text("自動的に鉄インゴットを生成します").color(NamedTextColor.GRAY),
                    Component.text("1分ごとに1個").color(NamedTextColor.YELLOW)
                ));
            } else if (type.equals("emerald")) {
                meta.displayName(Component.text("エメラルドCompactFarms").color(NamedTextColor.BLACK));
                meta.lore(List.of(
                    Component.text("自動的にエメラルドを生成します").color(NamedTextColor.GRAY),
                    Component.text("1分ごとに1個").color(NamedTextColor.YELLOW)
                ));
            } else if (type.equals("gunpowder")) {
                meta.displayName(Component.text("火薬CompactFarms").color(NamedTextColor.BLACK));
                meta.lore(List.of(
                    Component.text("自動的に火薬を生成します").color(NamedTextColor.GRAY),
                    Component.text("1分ごとに1個").color(NamedTextColor.YELLOW)
                ));
            } else if (type.equals("experience")) {
                meta.displayName(Component.text("経験値CompactFarms").color(NamedTextColor.BLACK));
                meta.lore(List.of(
                    Component.text("自動的に経験値瓶を生成します").color(NamedTextColor.GRAY),
                    Component.text("1分ごとに1個").color(NamedTextColor.YELLOW)
                ));
            }
            NamespacedKey cfKey = new NamespacedKey(this, "compactfarms");
            meta.getPersistentDataContainer().set(cfKey, PersistentDataType.BOOLEAN, true);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    @Override
    public void onDisable() {
        ResourceGenerator.getInstance(this).stop();
        getLogger().info("CompactFarms has been disabled!");
    }
    
    public static CompactFarms getInstance() {
        return instance;
    }
}
