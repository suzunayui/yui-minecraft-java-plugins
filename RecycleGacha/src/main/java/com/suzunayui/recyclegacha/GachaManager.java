package com.suzunayui.recyclegacha;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Hopper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class GachaManager {
    
    private final RecycleGacha plugin;
    private final NamespacedKey gachaKey;
    private final NamespacedKey pointsKey;
    private final NamespacedKey ownerKey;
    private final Map<Location, Integer> gachaPoints = new HashMap<>();
    
    public GachaManager(RecycleGacha plugin) {
        this.plugin = plugin;
        this.gachaKey = new NamespacedKey(plugin, "is_gacha");
        this.pointsKey = new NamespacedKey(plugin, "points");
        this.ownerKey = new NamespacedKey(plugin, "owner");
    }
    
    public boolean isGachaHopper(Hopper hopper) {
        PersistentDataContainer pdc = hopper.getPersistentDataContainer();
        return pdc.has(gachaKey, PersistentDataType.BOOLEAN);
    }
    
    public void registerGacha(Player player, Hopper hopper) {
        Block below = hopper.getBlock().getRelative(0, -1, 0);
        if (below.getType() != Material.CHEST) {
            Component message = LegacyComponentSerializer.legacySection().deserialize("§c下にチェストが必要です。");
            player.sendMessage(message);
            return;
        }
        
        if (isGachaHopper(hopper)) {
            Component message = LegacyComponentSerializer.legacySection().deserialize("§cこのホッパーは既にガチャ機として登録されています。");
            player.sendMessage(message);
            return;
        }
        
        PersistentDataContainer pdc = hopper.getPersistentDataContainer();
        pdc.set(gachaKey, PersistentDataType.BOOLEAN, true);
        pdc.set(pointsKey, PersistentDataType.INTEGER, 0);
        pdc.set(ownerKey, PersistentDataType.STRING, player.getUniqueId().toString());
        hopper.update();
        
        gachaPoints.put(hopper.getLocation(), 0);
        
        Component message = LegacyComponentSerializer.legacySection().deserialize("§aガチャ機を登録しました！アイテムを入れてリサイクルしましょう。");
        player.sendMessage(message);
    }
    
    public void unregisterGacha(Hopper hopper) {
        PersistentDataContainer pdc = hopper.getPersistentDataContainer();
        pdc.remove(gachaKey);
        pdc.remove(pointsKey);
        pdc.remove(ownerKey);
        hopper.update();
        
        gachaPoints.remove(hopper.getLocation());
    }
    
    public int getPoints(Hopper hopper) {
        PersistentDataContainer pdc = hopper.getPersistentDataContainer();
        if (pdc.has(pointsKey, PersistentDataType.INTEGER)) {
            return pdc.get(pointsKey, PersistentDataType.INTEGER);
        }
        return 0;
    }
    
    public UUID getOwner(Hopper hopper) {
        PersistentDataContainer pdc = hopper.getPersistentDataContainer();
        if (pdc.has(ownerKey, PersistentDataType.STRING)) {
            String uuidString = pdc.get(ownerKey, PersistentDataType.STRING);
            if (uuidString != null) {
                return UUID.fromString(uuidString);
            }
        }
        return null;
    }
    
    public void addPoints(Hopper hopper, int points) {
        int current = getPoints(hopper);
        int newPoints = current + points;
        
        PersistentDataContainer pdc = hopper.getPersistentDataContainer();
        pdc.set(pointsKey, PersistentDataType.INTEGER, newPoints);
        hopper.update();
        
        gachaPoints.put(hopper.getLocation(), newPoints);
    }
    
    public int getItemPoints(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return 0;
        }
        
        String itemName = item.getType().name();
        return plugin.getConfig().getInt("points." + itemName, 0);
    }
    
    public boolean tryGacha(Hopper hopper, Player player) {
        int requiredPoints = plugin.getConfig().getInt("required-points", 100);
        int currentPoints = getPoints(hopper);
        
        if (currentPoints >= requiredPoints) {
            performGacha(hopper, player);
            
            PersistentDataContainer pdc = hopper.getPersistentDataContainer();
            pdc.set(pointsKey, PersistentDataType.INTEGER, 0);
            hopper.update();
            
            gachaPoints.put(hopper.getLocation(), 0);
            
            return true;
        }
        
        return false;
    }
    
    private void performGacha(Hopper hopper, Player player) {
        Block below = hopper.getBlock().getRelative(0, -1, 0);
        if (below.getType() != Material.CHEST) {
            return;
        }
        
        Chest chest = (Chest) below.getState();
        Inventory inventory = chest.getInventory();
        
        String[] result = rollReward();
        if (result != null) {
            ItemStack reward = createRewardItem(result[0], Integer.parseInt(result[1]));
            inventory.addItem(reward);
            
            String itemName = formatItemName(result[0]);
            String rarity = result[2];
            
            // 本人に結果を表示
            String resultMessage = "§aリサイクル結果: " + itemName + " x" + result[1];
            Component resultComponent = LegacyComponentSerializer.legacySection().deserialize(resultMessage);
            player.sendMessage(resultComponent);
            
            World world = hopper.getWorld();
            world.playSound(hopper.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            world.spawnParticle(Particle.HAPPY_VILLAGER, hopper.getLocation().add(0.5, 0.5, 0.5), 20);
            
            // 大当たりの場合は全員に通知
            if (rarity.equals("JACKPOT")) {
                String message = "§6§l🎉 " + player.getName() + "さんがリサイクルガチャで" + itemName + "を当てました！";
                Component component = LegacyComponentSerializer.legacySection().deserialize(message);
                Bukkit.broadcast(component);
            }
        }
    }
    
    private ItemStack createRewardItem(String materialName, int amount) {
        Material material = Material.valueOf(materialName);
        
        if (material == Material.ENCHANTED_BOOK) {
            return createRandomEnchantedBook();
        }
        
        return new ItemStack(material, amount);
    }
    
    private String formatItemName(String materialName) {
        Material material = Material.valueOf(materialName);
        
        // アイテム名のマッピング
        return switch (material) {
            case EMERALD_BLOCK -> "エメラルドブロック";
            case DIAMOND -> "ダイヤモンド";
            case ENCHANTED_BOOK -> "エンチャント本";
            case EMERALD -> "エメラルド";
            case GOLD_INGOT -> "金インゴット";
            case IRON_INGOT -> "鉄インゴット";
            case FIREWORK_ROCKET -> "花火";
            case GOLDEN_APPLE -> "金のリンゴ";
            case BREAD -> "パン";
            case COOKED_BEEF -> "ステーキ";
            case COOKED_CHICKEN -> "焼き鳥";
            case ARROW -> "矢";
            case TORCH -> "松明";
            case EXPERIENCE_BOTTLE -> "経験値瓶";
            case ENDER_PEARL -> "エンダーパール";
            case BONE_MEAL -> "骨粉";
            case STRING -> "糸";
            case LEATHER -> "革";
            case COAL -> "石炭";
            case REDSTONE -> "レッドストーン";
            case LAPIS_LAZULI -> "ラピスラズリ";
            default -> material.name().toLowerCase().replace("_", " ");
        };
    }
    
    private String[] rollReward() {
        Random random = new Random();
        
        // まずレアリティを決定
        double roll = random.nextDouble() * 100;
        String rarity;
        
        if (roll < 1.0) { // 0.1～1%
            rarity = "JACKPOT";
        } else if (roll < 6.0) { // 1～5%
            rarity = "RARE";
        } else if (roll < 36.0) { // 10～30%
            rarity = "COMMON";
        } else { // 残り
            rarity = "USELESS";
        }
        
        ConfigurationSection raritySection = plugin.getConfig().getConfigurationSection("rewards." + rarity);
        if (raritySection == null) {
            return null;
        }
        
        // そのレアリティの中から抽選
        List<String> materials = new ArrayList<>();
        List<Integer> amounts = new ArrayList<>();
        List<Double> chances = new ArrayList<>();
        double totalChance = 0;
        
        for (String key : raritySection.getKeys(false)) {
            try {
                Material.valueOf(key); // バリデーション
                int amount = raritySection.getInt(key + ".amount", 1);
                double chance = raritySection.getDouble(key + ".chance", 1);
                
                materials.add(key);
                amounts.add(amount);
                chances.add(chance);
                totalChance += chance;
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Unknown material in rewards: " + key);
            }
        }
        
        if (materials.isEmpty() || totalChance == 0) {
            return null;
        }
        
        double rarityRoll = random.nextDouble() * totalChance;
        double cumulative = 0;
        
        for (int i = 0; i < materials.size(); i++) {
            cumulative += chances.get(i);
            if (rarityRoll < cumulative) {
                return new String[]{materials.get(i), String.valueOf(amounts.get(i)), rarity};
            }
        }
        
        return new String[]{materials.get(0), String.valueOf(amounts.get(0)), rarity};
    }
    
    private ItemStack createRandomEnchantedBook() {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        var meta = (org.bukkit.inventory.meta.EnchantmentStorageMeta) book.getItemMeta();
        
        // 修繕以外のエンチャントリスト
        List<org.bukkit.enchantments.Enchantment> enchantments = List.of(
            org.bukkit.enchantments.Enchantment.SHARPNESS,
            org.bukkit.enchantments.Enchantment.SMITE,
            org.bukkit.enchantments.Enchantment.BANE_OF_ARTHROPODS,
            org.bukkit.enchantments.Enchantment.KNOCKBACK,
            org.bukkit.enchantments.Enchantment.FIRE_ASPECT,
            org.bukkit.enchantments.Enchantment.LOOTING,
            org.bukkit.enchantments.Enchantment.SWEEPING_EDGE,
            org.bukkit.enchantments.Enchantment.EFFICIENCY,
            org.bukkit.enchantments.Enchantment.SILK_TOUCH,
            org.bukkit.enchantments.Enchantment.UNBREAKING,
            org.bukkit.enchantments.Enchantment.FORTUNE,
            org.bukkit.enchantments.Enchantment.POWER,
            org.bukkit.enchantments.Enchantment.PUNCH,
            org.bukkit.enchantments.Enchantment.FLAME,
            org.bukkit.enchantments.Enchantment.INFINITY,
            org.bukkit.enchantments.Enchantment.LUCK_OF_THE_SEA,
            org.bukkit.enchantments.Enchantment.LURE,
            org.bukkit.enchantments.Enchantment.LOYALTY,
            org.bukkit.enchantments.Enchantment.IMPALING,
            org.bukkit.enchantments.Enchantment.RIPTIDE,
            org.bukkit.enchantments.Enchantment.CHANNELING,
            org.bukkit.enchantments.Enchantment.MULTISHOT,
            org.bukkit.enchantments.Enchantment.QUICK_CHARGE,
            org.bukkit.enchantments.Enchantment.PIERCING,
            org.bukkit.enchantments.Enchantment.PROTECTION,
            org.bukkit.enchantments.Enchantment.FIRE_PROTECTION,
            org.bukkit.enchantments.Enchantment.BLAST_PROTECTION,
            org.bukkit.enchantments.Enchantment.PROJECTILE_PROTECTION,
            org.bukkit.enchantments.Enchantment.FEATHER_FALLING,
            org.bukkit.enchantments.Enchantment.THORNS,
            org.bukkit.enchantments.Enchantment.RESPIRATION,
            org.bukkit.enchantments.Enchantment.AQUA_AFFINITY,
            org.bukkit.enchantments.Enchantment.DEPTH_STRIDER,
            org.bukkit.enchantments.Enchantment.FROST_WALKER,
            org.bukkit.enchantments.Enchantment.SOUL_SPEED,
            org.bukkit.enchantments.Enchantment.SWIFT_SNEAK
        );
        
        Random random = new Random();
        org.bukkit.enchantments.Enchantment enchantment = enchantments.get(random.nextInt(enchantments.size()));
        int level = random.nextInt(enchantment.getMaxLevel()) + 1;
        
        meta.addStoredEnchant(enchantment, level, true);
        book.setItemMeta(meta);
        
        return book;
    }
    
    public void loadGachaPoints() {
        gachaPoints.clear();
        
        for (World world : Bukkit.getWorlds()) {
            for (var chunk : world.getLoadedChunks()) {
                for (var blockState : chunk.getTileEntities()) {
                    if (blockState instanceof Hopper hopper) {
                        if (isGachaHopper(hopper)) {
                            gachaPoints.put(hopper.getLocation(), getPoints(hopper));
                        }
                    }
                }
            }
        }
    }
}
