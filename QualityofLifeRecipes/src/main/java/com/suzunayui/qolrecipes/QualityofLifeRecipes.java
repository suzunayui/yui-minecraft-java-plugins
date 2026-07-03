package com.suzunayui.qolrecipes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.Map;

public class QualityofLifeRecipes extends JavaPlugin {
    
    private static QualityofLifeRecipes instance;
    
    @Override
    public void onEnable() {
        instance = this;
        
        registerShulkerBoxRecipes();
        
        getLogger().info("QualityofLifeRecipes has been enabled!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("QualityofLifeRecipes has been disabled!");
    }
    
    private void registerShulkerBoxRecipes() {
        Map<Material, Material> woolToShulker = new LinkedHashMap<>();
        woolToShulker.put(Material.WHITE_WOOL, Material.WHITE_SHULKER_BOX);
        woolToShulker.put(Material.ORANGE_WOOL, Material.ORANGE_SHULKER_BOX);
        woolToShulker.put(Material.MAGENTA_WOOL, Material.MAGENTA_SHULKER_BOX);
        woolToShulker.put(Material.LIGHT_BLUE_WOOL, Material.LIGHT_BLUE_SHULKER_BOX);
        woolToShulker.put(Material.YELLOW_WOOL, Material.YELLOW_SHULKER_BOX);
        woolToShulker.put(Material.LIME_WOOL, Material.LIME_SHULKER_BOX);
        woolToShulker.put(Material.PINK_WOOL, Material.PINK_SHULKER_BOX);
        woolToShulker.put(Material.GRAY_WOOL, Material.GRAY_SHULKER_BOX);
        woolToShulker.put(Material.LIGHT_GRAY_WOOL, Material.LIGHT_GRAY_SHULKER_BOX);
        woolToShulker.put(Material.CYAN_WOOL, Material.CYAN_SHULKER_BOX);
        woolToShulker.put(Material.PURPLE_WOOL, Material.PURPLE_SHULKER_BOX);
        woolToShulker.put(Material.BLUE_WOOL, Material.BLUE_SHULKER_BOX);
        woolToShulker.put(Material.BROWN_WOOL, Material.BROWN_SHULKER_BOX);
        woolToShulker.put(Material.GREEN_WOOL, Material.GREEN_SHULKER_BOX);
        woolToShulker.put(Material.RED_WOOL, Material.RED_SHULKER_BOX);
        woolToShulker.put(Material.BLACK_WOOL, Material.BLACK_SHULKER_BOX);
        
        for (Map.Entry<Material, Material> entry : woolToShulker.entrySet()) {
            Material wool = entry.getKey();
            Material shulker = entry.getValue();
            String colorName = wool.name().replace("_WOOL", "").toLowerCase();
            
            NamespacedKey key = new NamespacedKey(this, colorName + "_shulker_box");
            ShapedRecipe recipe = new ShapedRecipe(key, new org.bukkit.inventory.ItemStack(shulker));
            recipe.shape("WWW", "WCW", "WWW");
            recipe.setIngredient('W', wool);
            recipe.setIngredient('C', Material.CHEST);
            Bukkit.addRecipe(recipe);
        }
        
        getLogger().info("Registered " + woolToShulker.size() + " shulker box recipes");
    }
    
    public static QualityofLifeRecipes getInstance() {
        return instance;
    }
}
