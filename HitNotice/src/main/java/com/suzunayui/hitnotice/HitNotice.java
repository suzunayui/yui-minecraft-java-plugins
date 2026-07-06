package com.suzunayui.hitnotice;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.block.TileState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HitNotice extends JavaPlugin implements Listener {

    private static final Map<Material, String> ITEM_NAME_JP = new HashMap<>();

    static {
        // 剣
        ITEM_NAME_JP.put(Material.WOODEN_SWORD, "木の剣");
        ITEM_NAME_JP.put(Material.STONE_SWORD, "石の剣");
        ITEM_NAME_JP.put(Material.IRON_SWORD, "鉄の剣");
        ITEM_NAME_JP.put(Material.GOLDEN_SWORD, "金の剣");
        ITEM_NAME_JP.put(Material.DIAMOND_SWORD, "ダイヤモンドの剣");
        ITEM_NAME_JP.put(Material.NETHERITE_SWORD, "ネザライトの剣");

        // 斧
        ITEM_NAME_JP.put(Material.WOODEN_AXE, "木の斧");
        ITEM_NAME_JP.put(Material.STONE_AXE, "石の斧");
        ITEM_NAME_JP.put(Material.IRON_AXE, "鉄の斧");
        ITEM_NAME_JP.put(Material.GOLDEN_AXE, "金の斧");
        ITEM_NAME_JP.put(Material.DIAMOND_AXE, "ダイヤモンドの斧");
        ITEM_NAME_JP.put(Material.NETHERITE_AXE, "ネザライトの斧");

        // ツルハシ
        ITEM_NAME_JP.put(Material.WOODEN_PICKAXE, "木のツルハシ");
        ITEM_NAME_JP.put(Material.STONE_PICKAXE, "石のツルハシ");
        ITEM_NAME_JP.put(Material.IRON_PICKAXE, "鉄のツルハシ");
        ITEM_NAME_JP.put(Material.GOLDEN_PICKAXE, "金のツルハシ");
        ITEM_NAME_JP.put(Material.DIAMOND_PICKAXE, "ダイヤモンドのツルハシ");
        ITEM_NAME_JP.put(Material.NETHERITE_PICKAXE, "ネザライトのツルハシ");

        // シャベル
        ITEM_NAME_JP.put(Material.WOODEN_SHOVEL, "木のシャベル");
        ITEM_NAME_JP.put(Material.STONE_SHOVEL, "石のシャベル");
        ITEM_NAME_JP.put(Material.IRON_SHOVEL, "鉄のシャベル");
        ITEM_NAME_JP.put(Material.GOLDEN_SHOVEL, "金のシャベル");
        ITEM_NAME_JP.put(Material.DIAMOND_SHOVEL, "ダイヤモンドのシャベル");
        ITEM_NAME_JP.put(Material.NETHERITE_SHOVEL, "ネザライトのシャベル");

        // クワ
        ITEM_NAME_JP.put(Material.WOODEN_HOE, "木のクワ");
        ITEM_NAME_JP.put(Material.STONE_HOE, "石のクワ");
        ITEM_NAME_JP.put(Material.IRON_HOE, "鉄のクワ");
        ITEM_NAME_JP.put(Material.GOLDEN_HOE, "金のクワ");
        ITEM_NAME_JP.put(Material.DIAMOND_HOE, "ダイヤモンドのクワ");
        ITEM_NAME_JP.put(Material.NETHERITE_HOE, "ネザライトのクワ");

        // 棍棒（メイス）
        ITEM_NAME_JP.put(Material.MACE, "棍棒");

        // 弓矢
        ITEM_NAME_JP.put(Material.BOW, "弓");
        ITEM_NAME_JP.put(Material.CROSSBOW, "クロスボウ");
        ITEM_NAME_JP.put(Material.ARROW, "矢");
        ITEM_NAME_JP.put(Material.SPECTRAL_ARROW, "スペクトラルアロー");
        ITEM_NAME_JP.put(Material.TIPPED_ARROW, "薬入り矢");

        // トライデント
        ITEM_NAME_JP.put(Material.TRIDENT, "トライデント");

        // 槍（1.21.5以降）
        tryAddMaterial("WOODEN_SPEAR", "木の槍");
        tryAddMaterial("STONE_SPEAR", "石の槍");
        tryAddMaterial("IRON_SPEAR", "鉄の槍");
        tryAddMaterial("GOLDEN_SPEAR", "金の槍");
        tryAddMaterial("DIAMOND_SPEAR", "ダイヤモンドの槍");
        tryAddMaterial("NETHERITE_SPEAR", "ネザライトの槍");
        tryAddMaterial("COPPER_SPEAR", "銅の槍");

        // 素手
        ITEM_NAME_JP.put(Material.AIR, "素手");

        // その他武器になりうるアイテム
        ITEM_NAME_JP.put(Material.SHIELD, "盾");
        ITEM_NAME_JP.put(Material.FISHING_ROD, "釣り竿");
        ITEM_NAME_JP.put(Material.FLINT_AND_STEEL, "火打石と鋼鉄");
        ITEM_NAME_JP.put(Material.SHEARS, "ハサミ");
        ITEM_NAME_JP.put(Material.SNOWBALL, "雪玉");
        ITEM_NAME_JP.put(Material.EGG, "卵");
        ITEM_NAME_JP.put(Material.ENDER_PEARL, "エンダーパール");
        ITEM_NAME_JP.put(Material.SPLASH_POTION, "スプラッシュポーション");
        ITEM_NAME_JP.put(Material.LINGERING_POTION, "残留ポーション");
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("HitNotice has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("HitNotice has been disabled!");
    }

    // 爆発の追跡用マップ（座標 -> 設置者UUID）
    private static final Map<String, UUID> explosionOwners = new HashMap<>();
    
    // 落下ブロックの追跡用マップ（座標 -> 設置者UUID）
    private static final Map<String, UUID> fallingBlockOwners = new HashMap<>();
    
    // スイートベリーの追跡用マップ（座標 -> 設置者UUID）
    private static final Map<String, UUID> sweetBerryOwners = new HashMap<>();
    
    // 引っ張り追跡用マップ（プレイヤーUUID -> 引っ張ったプレイヤーUUID）
    private static final Map<UUID, UUID> pullTrackers = new HashMap<>();
    private static final long PULL_TRACK_DURATION_MS = 10000; // 10秒間追跡
    
    // 引っ張り時刻記録用マップ（プレイヤーUUID -> 引っ張られた時刻）
    private static final Map<UUID, Long> pullTimestamps = new HashMap<>();
    
    // クリーパー通知のクールダウン用マップ（プレイヤーUUID -> 最終通知時刻）
    private static final Map<UUID, Long> creeperCooldowns = new HashMap<>();
    private static final long CREEPER_COOLDOWN_MS = 5000; // 5秒

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Material type = block.getType();
        
        // リスポーンアンカー、エンドクリスタル、ベッドの設置者を記録
        if (type == Material.RESPAWN_ANCHOR || 
            type == Material.END_CRYSTAL ||
            type.toString().endsWith("_BED")) {
            
            String key = locationToKey(block.getLocation());
            explosionOwners.put(key, event.getPlayer().getUniqueId());
        }
        
        // 金床と鍾乳石の設置者を記録
        if (type == Material.ANVIL || 
            type == Material.CHIPPED_ANVIL || 
            type == Material.DAMAGED_ANVIL ||
            type == Material.POINTED_DRIPSTONE) {
            
            String key = locationToKey(block.getLocation());
            fallingBlockOwners.put(key, event.getPlayer().getUniqueId());
        }
        
        // 砂、砂利、コンクリートパウダーの設置者を記録
        if (type == Material.SAND || 
            type == Material.GRAVEL ||
            type == Material.RED_SAND ||
            type.toString().contains("_CONCRETE_POWDER")) {
            
            String key = locationToKey(block.getLocation());
            fallingBlockOwners.put(key, event.getPlayer().getUniqueId());
        }
        
        // スイートベリーの茂みの設置者を記録
        if (type == Material.SWEET_BERRY_BUSH) {
            String key = locationToKey(block.getLocation());
            sweetBerryOwners.put(key, event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // リスポーンアンカーやベッドの右クリックで爆発する可能性がある
        if (event.getClickedBlock() == null) return;
        
        Block block = event.getClickedBlock();
        Material type = block.getType();
        
        if (type == Material.RESPAWN_ANCHOR || type.toString().endsWith("_BED")) {
            // 設置者情報を更新（最後に触った人を記録）
            String key = locationToKey(block.getLocation());
            explosionOwners.put(key, event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (entity == null) return;
        
        // クリーパーの爆発の場合
        if (entity.getType() == EntityType.CREEPER) {
            // 近くにいたプレイヤーを検索（10ブロック以内）
            for (Entity nearby : entity.getNearbyEntities(10, 10, 10)) {
                if (nearby instanceof Player player) {
                    UUID playerId = player.getUniqueId();
                    long now = System.currentTimeMillis();
                    Long lastNotify = creeperCooldowns.get(playerId);
                    
                    // クールダウンチェック
                    if (lastNotify == null || now - lastNotify >= CREEPER_COOLDOWN_MS) {
                        String message = String.format("§c%sさんを狙っていたクリーパーが爆発しました", player.getName());
                        for (Player onlinePlayer : getServer().getOnlinePlayers()) {
                            onlinePlayer.sendMessage(message);
                        }
                        creeperCooldowns.put(playerId, now);
                        break; // 最初のプレイヤーのみ通知
                    }
                    break;
                }
            }
            return;
        }
        
        // エンドクリスタルまたはファイアチャージの爆発の場合
        if (entity.getType() == EntityType.END_CRYSTAL || 
            entity.getType() == EntityType.FIREBALL ||
            entity.getType() == EntityType.SMALL_FIREBALL) {
            
            // 発射者を取得
            if (entity instanceof Projectile projectile) {
                ProjectileSource shooter = projectile.getShooter();
                if (shooter instanceof Player player) {
                    String key = locationToKey(event.getLocation());
                    explosionOwners.put(key, player.getUniqueId());
                }
            }
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        // クリーパーがプレイヤーをターゲットにした時
        if (event.getEntity().getType() == EntityType.CREEPER && event.getTarget() instanceof Player player) {
            UUID playerId = player.getUniqueId();
            long now = System.currentTimeMillis();
            Long lastNotify = creeperCooldowns.get(playerId);
            
            // クールダウンチェック
            if (lastNotify == null || now - lastNotify >= CREEPER_COOLDOWN_MS) {
                String message = String.format("§cクリーパーが%sさんを狙っています", player.getName());
                for (Player onlinePlayer : getServer().getOnlinePlayers()) {
                    onlinePlayer.sendMessage(message);
                }
                creeperCooldowns.put(playerId, now);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damagerEntity = event.getDamager();
        Entity damagedEntity = event.getEntity();

        // 被害者がプレイヤーの場合のみ処理
        if (!(damagedEntity instanceof Player damaged)) {
            return;
        }

        // 攻撃者を取得（遠距離攻撃の場合は射撃者を取得）
        Player damager;
        String weaponName;

        if (damagerEntity instanceof Player playerDamager) {
            damager = playerDamager;
            
            // 火によるダメージの場合
            if (event.getCause() == org.bukkit.event.entity.EntityDamageEvent.DamageCause.FIRE ||
                event.getCause() == org.bukkit.event.entity.EntityDamageEvent.DamageCause.FIRE_TICK) {
                weaponName = "火";
            } else {
                ItemStack itemInHand = damager.getInventory().getItemInMainHand();
                Material material = itemInHand.getType();
                weaponName = getJapaneseItemName(material);
            }
        } else if (damagerEntity instanceof Projectile projectile) {
            ProjectileSource shooter = projectile.getShooter();
            if (shooter instanceof Player playerShooter) {
                damager = playerShooter;
                // 投射体の種類から武器名を推測
                weaponName = getProjectileWeaponName(projectile.getType());
            } else if (shooter instanceof BlockProjectileSource blockSource) {
                // ディスペンサーからの攻撃の場合
                Block block = blockSource.getBlock();
                if (block.getType() == Material.DISPENSER) {
                    String ownerName = getDispenserOwnerName(block);
                    if (ownerName != null) {
                        // ディスペンサーの設置者名でメッセージを作成
                        String message = String.format("§e%sさんがディスペンサーの%sで%sさんを攻撃しました",
                                ownerName, getProjectileWeaponName(projectile.getType()), damaged.getName());
                        for (Player onlinePlayer : getServer().getOnlinePlayers()) {
                            onlinePlayer.sendMessage(message);
                        }
                        return;
                    }
                }
                return;
            } else {
                return;
            }
        } else if (damagerEntity instanceof FallingBlock fallingBlock) {
            // 金床や鍾乳石の落下によるダメージの場合
            Material blockType = fallingBlock.getBlockData().getMaterial();
            String blockName = getFallingBlockName(blockType);
            
            // 落下元の座標（ブロックが落下を開始した位置の下のブロック）を推測
            Location fallLocation = fallingBlock.getLocation();
            String key = locationToKey(fallLocation);
            UUID ownerUuid = fallingBlockOwners.get(key);
            
            if (ownerUuid != null) {
                Player owner = Bukkit.getPlayer(ownerUuid);
                if (owner != null && !owner.equals(damaged)) {
                    String message = String.format("§e%sさんが降らせた%sが%sさんに当たりました",
                            owner.getName(), blockName, damaged.getName());
                    for (Player onlinePlayer : getServer().getOnlinePlayers()) {
                        onlinePlayer.sendMessage(message);
                    }
                    // 使用済みのエントリを削除
                    fallingBlockOwners.remove(key);
                    return;
                }
            }
            return;
        } else {
            // 爆発によるダメージの場合
            if (event.getCause() == org.bukkit.event.entity.EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
                event.getCause() == org.bukkit.event.entity.EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
                
                String key = locationToKey(damaged.getLocation());
                UUID ownerUuid = explosionOwners.get(key);
                
                if (ownerUuid != null) {
                    Player owner = Bukkit.getPlayer(ownerUuid);
                    if (owner != null && !owner.equals(damaged)) {
                        String explosionType = getExplosionTypeName(damagerEntity);
                        String message = String.format("§e%sさんが%sで%sさんを攻撃しました",
                                owner.getName(), explosionType, damaged.getName());
                        for (Player onlinePlayer : getServer().getOnlinePlayers()) {
                            onlinePlayer.sendMessage(message);
                        }
                        
                        // 使用済みのエントリを削除
                        explosionOwners.remove(key);
                        return;
                    }
                }
            }
            return;
        }

        // 自分自身は除外
        if (damager.equals(damaged)) {
            return;
        }

        // メッセージを作成
        String message = String.format("§e%sさんが%sで%sさんを攻撃しました",
                damager.getName(), weaponName, damaged.getName());

        // 全プレイヤーにメッセージを送信
        for (Player onlinePlayer : getServer().getOnlinePlayers()) {
            onlinePlayer.sendMessage(message);
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        // 釣り竿でエンティティを引っ張った時
        if (event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
            Entity caught = event.getCaught();
            if (caught instanceof Player pulledPlayer) {
                Player fisher = event.getPlayer();
                UUID pulledId = pulledPlayer.getUniqueId();
                UUID fisherId = fisher.getUniqueId();
                
                // 引っ張ったプレイヤーを記録
                pullTrackers.put(pulledId, fisherId);
                pullTimestamps.put(pulledId, System.currentTimeMillis());
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        // リードでエンティティを引っ張った時（リードの使用はEntityInteractEventでは検出できないため、
        // 代わりにEntityLeashEventを使用する必要があるが、プレイヤーがリードで引っ張る場合は
        // 直接検出が難しいため、簡易的に実装）
        // 実際にはリードの引っ張りは検出が難しいため、この機能は制限付き
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        // 被害者がプレイヤーの場合のみ処理
        if (!(event.getEntity() instanceof Player damaged)) {
            return;
        }

        // 落下ダメージの場合、引っ張られていたかチェック
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            UUID damagedId = damaged.getUniqueId();
            UUID pullerId = pullTrackers.get(damagedId);
            Long pullTime = pullTimestamps.get(damagedId);
            
            if (pullerId != null && pullTime != null) {
                long now = System.currentTimeMillis();
                // 3秒以内に引っ張られていた場合
                if (now - pullTime <= PULL_TRACK_DURATION_MS) {
                    Player puller = Bukkit.getPlayer(pullerId);
                    if (puller != null && !puller.equals(damaged)) {
                        String message = String.format("§e%sさんが%sさんを釣り竿で引っ張って落下させました",
                                puller.getName(), damaged.getName());
                        for (Player onlinePlayer : getServer().getOnlinePlayers()) {
                            onlinePlayer.sendMessage(message);
                        }
                    }
                }
                // 追跡情報を削除
                pullTrackers.remove(damagedId);
                pullTimestamps.remove(damagedId);
            }
        }

        // スイートベリーによるダメージの場合
        if (event.getCause() == EntityDamageEvent.DamageCause.CONTACT) {
            // プレイヤーの下のブロックをチェック
            Location loc = damaged.getLocation();
            Location blockLoc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            Block block = blockLoc.getBlock();
            
            if (block.getType() == Material.SWEET_BERRY_BUSH) {
                String key = locationToKey(block.getLocation());
                UUID ownerUuid = sweetBerryOwners.get(key);
                
                if (ownerUuid != null) {
                    Player owner = Bukkit.getPlayer(ownerUuid);
                    if (owner != null && !owner.equals(damaged)) {
                        String message = String.format("§e%sさんが植えたスイートベリーが%sさんに刺さりました",
                                owner.getName(), damaged.getName());
                        for (Player onlinePlayer : getServer().getOnlinePlayers()) {
                            onlinePlayer.sendMessage(message);
                        }
                    }
                }
            }
        }
    }

    private static String getJapaneseItemName(Material material) {
        return ITEM_NAME_JP.getOrDefault(material, "素手");
    }

    private static void tryAddMaterial(String materialName, String japaneseName) {
        try {
            Material material = Material.valueOf(materialName);
            ITEM_NAME_JP.put(material, japaneseName);
        } catch (IllegalArgumentException ignored) {
            // Materialが存在しないバージョンの場合は無視
        }
    }

    private String getProjectileWeaponName(org.bukkit.entity.EntityType type) {
        return switch (type) {
            case ARROW -> "矢";
            case SPECTRAL_ARROW -> "スペクトラルアロー";
            case TRIDENT -> "トライデント";
            case SNOWBALL -> "雪玉";
            case EGG -> "卵";
            case FIREBALL -> "ガストの火球";
            case SMALL_FIREBALL -> "ブレイズの火球";
            case WITHER_SKULL -> "ウィザースカル";
            case SHULKER_BULLET -> "シュルカーの弾";
            case LLAMA_SPIT -> "ラマの唾";
            default -> "投擲物";
        };
    }

    private String getDispenserOwnerName(Block block) {
        if (!(block.getState() instanceof TileState tileState)) {
            return null;
        }

        PersistentDataContainer pdc = tileState.getPersistentDataContainer();
        
        // NoDestroyプラグインのインスタンスを取得
        Plugin noDestroyPlugin = Bukkit.getPluginManager().getPlugin("NoDestroy");
        if (noDestroyPlugin == null) {
            return null;
        }

        NamespacedKey key = new NamespacedKey(noDestroyPlugin, "dispenser-owner");
        
        if (!pdc.has(key, PersistentDataType.STRING)) {
            return null;
        }

        String ownerUuid = pdc.get(key, PersistentDataType.STRING);
        if (ownerUuid == null) {
            return null;
        }

        try {
            UUID uuid = UUID.fromString(ownerUuid);
            Player owner = Bukkit.getPlayer(uuid);
            if (owner != null) {
                return owner.getName();
            }
            // オフラインプレイヤーの場合
            var offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            return offlinePlayer.getName();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String locationToKey(Location location) {
        return location.getWorld().getName() + ":" + 
               location.getBlockX() + ":" + 
               location.getBlockY() + ":" + 
               location.getBlockZ();
    }

    private String getExplosionTypeName(Entity entity) {
        if (entity == null) {
            return "爆発";
        }
        
        return switch (entity.getType()) {
            case END_CRYSTAL -> "エンドクリスタル";
            case FIREBALL -> "ガストの火球";
            case SMALL_FIREBALL -> "ブレイズの火球";
            case WITHER_SKULL -> "ウィザースカル";
            case TNT, TNT_MINECART -> "TNT";
            case CREEPER -> "クリーパー";
            default -> "爆発";
        };
    }

    private String getFallingBlockName(Material material) {
        return switch (material) {
            case ANVIL -> "金床";
            case CHIPPED_ANVIL -> "欠けた金床";
            case DAMAGED_ANVIL -> "壊れかけの金床";
            case POINTED_DRIPSTONE -> "鍾乳石";
            case SAND -> "砂";
            case RED_SAND -> "赤い砂";
            case GRAVEL -> "砂利";
            default -> {
                if (material.toString().contains("_CONCRETE_POWDER")) {
                    yield "コンクリートパウダー";
                }
                yield "落下ブロック";
            }
        };
    }
}
