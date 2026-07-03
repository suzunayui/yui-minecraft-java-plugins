package com.suzunayui.ultimatelibrarian;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class LibrarianManager {
    
    private final UltimateLibrarian plugin;
    private final List<UUID> spawnedNPCs = new ArrayList<>();
    private final NamespacedKey npcTypeKey;
    
    public LibrarianManager(UltimateLibrarian plugin) {
        this.plugin = plugin;
        this.npcTypeKey = new NamespacedKey(plugin, "npc_type");
    }
    
    public void spawnNPC(Player player, String type) {
        World world = player.getWorld();
        Location loc = player.getLocation().add(player.getLocation().getDirection().multiply(2));
        loc.setY(player.getLocation().getY());
        
        Villager villager = world.spawn(loc, Villager.class);
        villager.setProfession(Villager.Profession.LIBRARIAN);
        villager.setVillagerLevel(5);
        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setSilent(true);
        villager.setCanPickupItems(false);
        
        villager.customName(getCustomName(type));
        villager.setCustomNameVisible(true);
        
        PersistentDataContainer pdc = villager.getPersistentDataContainer();
        pdc.set(npcTypeKey, PersistentDataType.STRING, type);
        
        setupTrades(villager, type);
        
        spawnedNPCs.add(villager.getUniqueId());
        
        player.sendMessage(Component.text("司書NPCを召喚しました！").color(NamedTextColor.GREEN));
    }
    
    public void removeNPCs(Player player) {
        int removed = 0;
        World world = player.getWorld();
        
        for (Entity entity : world.getEntities()) {
            if (entity instanceof Villager villager) {
                PersistentDataContainer pdc = villager.getPersistentDataContainer();
                if (pdc.has(npcTypeKey, PersistentDataType.STRING)) {
                    villager.remove();
                    removed++;
                }
            }
        }
        
        spawnedNPCs.clear();
        player.sendMessage(Component.text(removed + "体の司書NPCを削除しました。").color(NamedTextColor.YELLOW));
    }
    
    public void removeAllNPCs() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Villager villager) {
                    PersistentDataContainer pdc = villager.getPersistentDataContainer();
                    if (pdc.has(npcTypeKey, PersistentDataType.STRING)) {
                        villager.remove();
                    }
                }
            }
        }
        spawnedNPCs.clear();
    }
    
    private Component getCustomName(String type) {
        return switch (type) {
            case "tool" -> Component.text("📚 道具司書").color(NamedTextColor.AQUA);
            case "weapon" -> Component.text("⚔️ 武器司書").color(NamedTextColor.RED);
            case "armor" -> Component.text("🛡️ 防具司書").color(NamedTextColor.YELLOW);
            case "ranged" -> Component.text("🏹 遠距離司書").color(NamedTextColor.GREEN);
            default -> Component.text("司書");
        };
    }
    
    private void setupTrades(Villager villager, String type) {
        List<MerchantRecipe> recipes = new ArrayList<>();
        
        switch (type) {
            case "tool" -> setupToolTrades(recipes);
            case "weapon" -> setupWeaponTrades(recipes);
            case "armor" -> setupArmorTrades(recipes);
            case "ranged" -> setupRangedTrades(recipes);
        }
        
        villager.setRecipes(recipes);
    }
    
    private void setupToolTrades(List<MerchantRecipe> recipes) {
        addTrade(recipes, Enchantment.MENDING, 1, "修繕");
        addTrade(recipes, Enchantment.EFFICIENCY, 5, "効率V");
        addTrade(recipes, Enchantment.UNBREAKING, 3, "耐久III");
        addTrade(recipes, Enchantment.FORTUNE, 3, "幸運III");
        addTrade(recipes, Enchantment.SILK_TOUCH, 1, "シルクタッチ");
    }
    
    private void setupWeaponTrades(List<MerchantRecipe> recipes) {
        addTrade(recipes, Enchantment.SHARPNESS, 5, "ダメージ増加V");
        addTrade(recipes, Enchantment.FIRE_ASPECT, 2, "火属性II");
        addTrade(recipes, Enchantment.LOOTING, 3, "ドロップ増加III");
        addTrade(recipes, Enchantment.KNOCKBACK, 2, "ノックバックII");
        addTrade(recipes, Enchantment.SMITE, 5, "アンデッド特効V");
        addTrade(recipes, Enchantment.BANE_OF_ARTHROPODS, 5, "虫殺しV");
    }
    
    private void setupArmorTrades(List<MerchantRecipe> recipes) {
        addTrade(recipes, Enchantment.PROTECTION, 4, "ダメージ軽減IV");
        addTrade(recipes, Enchantment.FIRE_PROTECTION, 4, "火炎耐性IV");
        addTrade(recipes, Enchantment.FEATHER_FALLING, 4, "落下耐性IV");
        addTrade(recipes, Enchantment.BLAST_PROTECTION, 4, "爆発耐性IV");
        addTrade(recipes, Enchantment.PROJECTILE_PROTECTION, 4, "飛び道具耐性IV");
        addTrade(recipes, Enchantment.THORNS, 3, "棘の鎧III");
        addTrade(recipes, Enchantment.DEPTH_STRIDER, 3, "水中歩行III");
        addTrade(recipes, Enchantment.AQUA_AFFINITY, 1, "水中採掘");
        addTrade(recipes, Enchantment.RESPIRATION, 3, "水中呼吸III");
    }
    
    private void setupRangedTrades(List<MerchantRecipe> recipes) {
        addTrade(recipes, Enchantment.POWER, 5, "パワーV");
        addTrade(recipes, Enchantment.FLAME, 1, "フレイム");
        addTrade(recipes, Enchantment.INFINITY, 1, "無限");
        addTrade(recipes, Enchantment.PUNCH, 2, "パンチII");
        addTrade(recipes, Enchantment.QUICK_CHARGE, 3, "クイックチャージIII");
        addTrade(recipes, Enchantment.MULTISHOT, 1, "マルチショット");
        addTrade(recipes, Enchantment.PIERCING, 4, "貫通IV");
        addTrade(recipes, Enchantment.LUCK_OF_THE_SEA, 3, "宝釣りIII");
        addTrade(recipes, Enchantment.LURE, 3, "誘惑III");
    }
    
    private void addTrade(List<MerchantRecipe> recipes, Enchantment enchantment, int level, String name) {
        ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) enchantedBook.getItemMeta();
        meta.addStoredEnchant(enchantment, level, true);
        enchantedBook.setItemMeta(meta);
        
        ItemStack emeralds = new ItemStack(Material.EMERALD, 64);
        
        MerchantRecipe recipe = new MerchantRecipe(enchantedBook, Integer.MAX_VALUE);
        recipe.addIngredient(emeralds);
        
        recipes.add(recipe);
    }
}
