package com.suzunayui.containersearch;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class JapaneseItemNames {
    
    private static final Map<String, Material> JAPANESE_TO_MATERIAL = new HashMap<>();
    private static final Map<Material, String> MATERIAL_TO_JAPANESE = new HashMap<>();
    
    static {
        addMapping("鉄インゴット", Material.IRON_INGOT);
        addMapping("金インゴット", Material.GOLD_INGOT);
        addMapping("ダイヤモンド", Material.DIAMOND);
        addMapping("エメラルド", Material.EMERALD);
        addMapping("ラピスラズリ", Material.LAPIS_LAZULI);
        addMapping("レッドストーン", Material.REDSTONE);
        addMapping("クォーツ", Material.QUARTZ);
        addMapping("石炭", Material.COAL);
        addMapping("木炭", Material.CHARCOAL);
        addMapping("ネザライトインゴット", Material.NETHERITE_INGOT);
        addMapping("ネザライトの欠片", Material.NETHERITE_SCRAP);
        addMapping("古代の残骸", Material.ANCIENT_DEBRIS);
        
        addMapping("オークの原木", Material.OAK_LOG);
        addMapping("トウヒの原木", Material.SPRUCE_LOG);
        addMapping("シラカバの原木", Material.BIRCH_LOG);
        addMapping("ジャングルの原木", Material.JUNGLE_LOG);
        addMapping("アカシアの原木", Material.ACACIA_LOG);
        addMapping("ダークオークの原木", Material.DARK_OAK_LOG);
        addMapping("マングローブの原木", Material.MANGROVE_LOG);
        addMapping("サクラの原木", Material.CHERRY_LOG);
        
        addMapping("オークの板材", Material.OAK_PLANKS);
        addMapping("トウヒの板材", Material.SPRUCE_PLANKS);
        addMapping("シラカバの板材", Material.BIRCH_PLANKS);
        addMapping("ジャングルの板材", Material.JUNGLE_PLANKS);
        addMapping("アカシアの板材", Material.ACACIA_PLANKS);
        addMapping("ダークオークの板材", Material.DARK_OAK_PLANKS);
        addMapping("マングローブの板材", Material.MANGROVE_PLANKS);
        addMapping("サクラの板材", Material.CHERRY_PLANKS);
        
        addMapping("丸石", Material.COBBLESTONE);
        addMapping("苔むした丸石", Material.MOSSY_COBBLESTONE);
        addMapping("石", Material.STONE);
        addMapping("花崗岩", Material.GRANITE);
        addMapping("磨かれた花崗岩", Material.POLISHED_GRANITE);
        addMapping("閃緑岩", Material.DIORITE);
        addMapping("磨かれた閃緑岩", Material.POLISHED_DIORITE);
        addMapping("安山岩", Material.ANDESITE);
        addMapping("磨かれた安山岩", Material.POLISHED_ANDESITE);
        addMapping("深層岩", Material.DEEPSLATE);
        addMapping("深層岩の丸石", Material.COBBLED_DEEPSLATE);
        addMapping("黒石", Material.BASALT);
        addMapping("玄武岩", Material.BASALT);
        addMapping("ネザーラック", Material.NETHERRACK);
        addMapping("エンドストーン", Material.END_STONE);
        
        addMapping("土", Material.DIRT);
        addMapping("粗い土", Material.COARSE_DIRT);
        addMapping("ポドゾル", Material.PODZOL);
        addMapping("草ブロック", Material.GRASS_BLOCK);
        addMapping("砂", Material.SAND);
        addMapping("赤い砂", Material.RED_SAND);
        addMapping("砂利", Material.GRAVEL);
        addMapping("粘土", Material.CLAY);
        addMapping("雪", Material.SNOW);
        addMapping("氷", Material.ICE);
        
        addMapping("オークの苗木", Material.OAK_SAPLING);
        addMapping("トウヒの苗木", Material.SPRUCE_SAPLING);
        addMapping("シラカバの苗木", Material.BIRCH_SAPLING);
        addMapping("ジャングルの苗木", Material.JUNGLE_SAPLING);
        addMapping("アカシアの苗木", Material.ACACIA_SAPLING);
        addMapping("ダークオークの苗木", Material.DARK_OAK_SAPLING);
        addMapping("マングローブの繁殖体", Material.MANGROVE_PROPAGULE);
        addMapping("サクラの苗木", Material.CHERRY_SAPLING);
        
        addMapping("オークの葉", Material.OAK_LEAVES);
        addMapping("トウヒの葉", Material.SPRUCE_LEAVES);
        addMapping("シラカバの葉", Material.BIRCH_LEAVES);
        addMapping("ジャングルの葉", Material.JUNGLE_LEAVES);
        addMapping("アカシアの葉", Material.ACACIA_LEAVES);
        addMapping("ダークオークの葉", Material.DARK_OAK_LEAVES);
        addMapping("マングローブの葉", Material.MANGROVE_LEAVES);
        addMapping("サクラの葉", Material.CHERRY_LEAVES);
        addMapping("アザレア", Material.AZALEA_LEAVES);
        addMapping("開花アザレアの葉", Material.FLOWERING_AZALEA_LEAVES);
        
        addMapping("小麦", Material.WHEAT);
        addMapping("小麦の種", Material.WHEAT_SEEDS);
        addMapping("パン", Material.BREAD);
        addMapping("ニンジン", Material.CARROT);
        addMapping("ジャガイモ", Material.POTATO);
        addMapping("ビートルート", Material.BEETROOT);
        addMapping("スイカ", Material.MELON);
        addMapping("カボチャ", Material.PUMPKIN);
        addMapping("サトウキビ", Material.SUGAR_CANE);
        addMapping("りんご", Material.APPLE);
        addMapping("金のりんご", Material.GOLDEN_APPLE);
        addMapping("エンチャントされた金のりんご", Material.ENCHANTED_GOLDEN_APPLE);
        
        addMapping("牛肉", Material.BEEF);
        addMapping("豚肉", Material.PORKCHOP);
        addMapping("羊肉", Material.MUTTON);
        addMapping("鶏肉", Material.CHICKEN);
        addMapping("兎肉", Material.RABBIT);
        addMapping("鱈", Material.COD);
        addMapping("鮭", Material.SALMON);
        addMapping("熱帯魚", Material.TROPICAL_FISH);
        addMapping("フグ", Material.PUFFERFISH);
        
        addMapping("火薬", Material.GUNPOWDER);
        addMapping("グロウストーンダスト", Material.GLOWSTONE_DUST);
        addMapping("プリズマリンの欠片", Material.PRISMARINE_SHARD);
        addMapping("プリズマリンクリスタル", Material.PRISMARINE_CRYSTALS);
        addMapping("ウサギの皮", Material.RABBIT_HIDE);
        addMapping("革", Material.LEATHER);
        addMapping("羽", Material.FEATHER);
        addMapping("骨", Material.BONE);
        addMapping("骨粉", Material.BONE_MEAL);
        addMapping("糸", Material.STRING);
        addMapping("蜘蛛の目", Material.SPIDER_EYE);
        addMapping("腐った肉", Material.ROTTEN_FLESH);
        addMapping("エンダーパール", Material.ENDER_PEARL);
        addMapping("ブレイズロッド", Material.BLAZE_ROD);
        addMapping("ブレイズパウダー", Material.BLAZE_POWDER);
        addMapping("ガストの涙", Material.GHAST_TEAR);
        addMapping("マグマクリーム", Material.MAGMA_CREAM);
        addMapping("ウィザースケルトンの頭蓋", Material.WITHER_SKELETON_SKULL);
        addMapping("ネザースター", Material.NETHER_STAR);
        addMapping("シェルカーボックス", Material.SHULKER_BOX);
        addMapping("ドラゴンの息", Material.DRAGON_BREATH);
        addMapping("エリトラ", Material.ELYTRA);
        addMapping("トライデント", Material.TRIDENT);
        addMapping("ハートの海", Material.HEART_OF_THE_SEA);
        addMapping("海洋の心", Material.HEART_OF_THE_SEA);
        
        addMapping("オークの木", Material.OAK_WOOD);
        addMapping("トウヒの木", Material.SPRUCE_WOOD);
        addMapping("シラカバの木", Material.BIRCH_WOOD);
        addMapping("ジャングルの木", Material.JUNGLE_WOOD);
        addMapping("アカシアの木", Material.ACACIA_WOOD);
        addMapping("ダークオークの木", Material.DARK_OAK_WOOD);
        
        addMapping("オークの階段", Material.OAK_STAIRS);
        addMapping("トウヒの階段", Material.SPRUCE_STAIRS);
        addMapping("シラカバの階段", Material.BIRCH_STAIRS);
        addMapping("ジャングルの階段", Material.JUNGLE_STAIRS);
        addMapping("アカシアの階段", Material.ACACIA_STAIRS);
        addMapping("ダークオークの階段", Material.DARK_OAK_STAIRS);
        
        addMapping("オークのハーフブロック", Material.OAK_SLAB);
        addMapping("トウヒのハーフブロック", Material.SPRUCE_SLAB);
        addMapping("シラカバのハーフブロック", Material.BIRCH_SLAB);
        addMapping("ジャングルのハーフブロック", Material.JUNGLE_SLAB);
        addMapping("アカシアのハーフブロック", Material.ACACIA_SLAB);
        addMapping("ダークオークのハーフブロック", Material.DARK_OAK_SLAB);
        
        addMapping("オークのフェンス", Material.OAK_FENCE);
        addMapping("トウヒのフェンス", Material.SPRUCE_FENCE);
        addMapping("シラカバのフェンス", Material.BIRCH_FENCE);
        addMapping("ジャングルのフェンス", Material.JUNGLE_FENCE);
        addMapping("アカシアのフェンス", Material.ACACIA_FENCE);
        addMapping("ダークオークのフェンス", Material.DARK_OAK_FENCE);
        
        addMapping("オークのドア", Material.OAK_DOOR);
        addMapping("トウヒのドア", Material.SPRUCE_DOOR);
        addMapping("シラカバのドア", Material.BIRCH_DOOR);
        addMapping("ジャングルのドア", Material.JUNGLE_DOOR);
        addMapping("アカシアのドア", Material.ACACIA_DOOR);
        addMapping("ダークオークのドア", Material.DARK_OAK_DOOR);
        
        addMapping("オークのトラップドア", Material.OAK_TRAPDOOR);
        addMapping("トウヒのトラップドア", Material.SPRUCE_TRAPDOOR);
        addMapping("シラカバのトラップドア", Material.BIRCH_TRAPDOOR);
        addMapping("ジャングルのトラップドア", Material.JUNGLE_TRAPDOOR);
        addMapping("アカシアのトラップドア", Material.ACACIA_TRAPDOOR);
        addMapping("ダークオークのトラップドア", Material.DARK_OAK_TRAPDOOR);
        
        addMapping("オークのボタン", Material.OAK_BUTTON);
        addMapping("トウヒのボタン", Material.SPRUCE_BUTTON);
        addMapping("シラカバのボタン", Material.BIRCH_BUTTON);
        addMapping("ジャングルのボタン", Material.JUNGLE_BUTTON);
        addMapping("アカシアのボタン", Material.ACACIA_BUTTON);
        addMapping("ダークオークのボタン", Material.DARK_OAK_BUTTON);
        
        addMapping("オークの感圧板", Material.OAK_PRESSURE_PLATE);
        addMapping("トウヒの感圧板", Material.SPRUCE_PRESSURE_PLATE);
        addMapping("シラカバの感圧板", Material.BIRCH_PRESSURE_PLATE);
        addMapping("ジャングルの感圧板", Material.JUNGLE_PRESSURE_PLATE);
        addMapping("アカシアの感圧板", Material.ACACIA_PRESSURE_PLATE);
        addMapping("ダークオークの感圧板", Material.DARK_OAK_PRESSURE_PLATE);
        
        addMapping("オークの看板", Material.OAK_SIGN);
        addMapping("トウヒの看板", Material.SPRUCE_SIGN);
        addMapping("シラカバの看板", Material.BIRCH_SIGN);
        addMapping("ジャングルの看板", Material.JUNGLE_SIGN);
        addMapping("アカシアの看板", Material.ACACIA_SIGN);
        addMapping("ダークオークの看板", Material.DARK_OAK_SIGN);
        
        addMapping("オークのチェスト", Material.CHEST);
        addMapping("エンダーチェスト", Material.ENDER_CHEST);
        
        addMapping("ガラス", Material.GLASS);
        addMapping("白色のガラス", Material.WHITE_STAINED_GLASS);
        addMapping("橙色のガラス", Material.ORANGE_STAINED_GLASS);
        addMapping("赤紫色のガラス", Material.MAGENTA_STAINED_GLASS);
        addMapping("空色のガラス", Material.LIGHT_BLUE_STAINED_GLASS);
        addMapping("黄色のガラス", Material.YELLOW_STAINED_GLASS);
        addMapping("黄緑色のガラス", Material.LIME_STAINED_GLASS);
        addMapping("桃色のガラス", Material.PINK_STAINED_GLASS);
        addMapping("灰色のガラス", Material.GRAY_STAINED_GLASS);
        addMapping("薄灰色のガラス", Material.LIGHT_GRAY_STAINED_GLASS);
        addMapping("青緑色のガラス", Material.CYAN_STAINED_GLASS);
        addMapping("紫色のガラス", Material.PURPLE_STAINED_GLASS);
        addMapping("青色のガラス", Material.BLUE_STAINED_GLASS);
        addMapping("茶色のガラス", Material.BROWN_STAINED_GLASS);
        addMapping("緑色のガラス", Material.GREEN_STAINED_GLASS);
        addMapping("赤色のガラス", Material.RED_STAINED_GLASS);
        addMapping("黒色のガラス", Material.BLACK_STAINED_GLASS);
        
        addMapping("白色の羊毛", Material.WHITE_WOOL);
        addMapping("橙色の羊毛", Material.ORANGE_WOOL);
        addMapping("赤紫色の羊毛", Material.MAGENTA_WOOL);
        addMapping("空色の羊毛", Material.LIGHT_BLUE_WOOL);
        addMapping("黄色の羊毛", Material.YELLOW_WOOL);
        addMapping("黄緑色の羊毛", Material.LIME_WOOL);
        addMapping("桃色の羊毛", Material.PINK_WOOL);
        addMapping("灰色の羊毛", Material.GRAY_WOOL);
        addMapping("薄灰色の羊毛", Material.LIGHT_GRAY_WOOL);
        addMapping("青緑色の羊毛", Material.CYAN_WOOL);
        addMapping("紫色の羊毛", Material.PURPLE_WOOL);
        addMapping("青色の羊毛", Material.BLUE_WOOL);
        addMapping("茶色の羊毛", Material.BROWN_WOOL);
        addMapping("緑色の羊毛", Material.GREEN_WOOL);
        addMapping("赤色の羊毛", Material.RED_WOOL);
        addMapping("黒色の羊毛", Material.BLACK_WOOL);
        
        addMapping("白色のコンクリート", Material.WHITE_CONCRETE);
        addMapping("橙色のコンクリート", Material.ORANGE_CONCRETE);
        addMapping("赤紫色のコンクリート", Material.MAGENTA_CONCRETE);
        addMapping("空色のコンクリート", Material.LIGHT_BLUE_CONCRETE);
        addMapping("黄色のコンクリート", Material.YELLOW_CONCRETE);
        addMapping("黄緑色のコンクリート", Material.LIME_CONCRETE);
        addMapping("桃色のコンクリート", Material.PINK_CONCRETE);
        addMapping("灰色のコンクリート", Material.GRAY_CONCRETE);
        addMapping("薄灰色のコンクリート", Material.LIGHT_GRAY_CONCRETE);
        addMapping("青緑色のコンクリート", Material.CYAN_CONCRETE);
        addMapping("紫色のコンクリート", Material.PURPLE_CONCRETE);
        addMapping("青色のコンクリート", Material.BLUE_CONCRETE);
        addMapping("茶色のコンクリート", Material.BROWN_CONCRETE);
        addMapping("緑色のコンクリート", Material.GREEN_CONCRETE);
        addMapping("赤色のコンクリート", Material.RED_CONCRETE);
        addMapping("黒色のコンクリート", Material.BLACK_CONCRETE);
        
        addMapping("白色のテラコッタ", Material.WHITE_TERRACOTTA);
        addMapping("橙色のテラコッタ", Material.ORANGE_TERRACOTTA);
        addMapping("赤紫色のテラコッタ", Material.MAGENTA_TERRACOTTA);
        addMapping("空色のテラコッタ", Material.LIGHT_BLUE_TERRACOTTA);
        addMapping("黄色のテラコッタ", Material.YELLOW_TERRACOTTA);
        addMapping("黄緑色のテラコッタ", Material.LIME_TERRACOTTA);
        addMapping("桃色のテラコッタ", Material.PINK_TERRACOTTA);
        addMapping("灰色のテラコッタ", Material.GRAY_TERRACOTTA);
        addMapping("薄灰色のテラコッタ", Material.LIGHT_GRAY_TERRACOTTA);
        addMapping("青緑色のテラコッタ", Material.CYAN_TERRACOTTA);
        addMapping("紫色のテラコッタ", Material.PURPLE_TERRACOTTA);
        addMapping("青色のテラコッタ", Material.BLUE_TERRACOTTA);
        addMapping("茶色のテラコッタ", Material.BROWN_TERRACOTTA);
        addMapping("緑色のテラコッタ", Material.GREEN_TERRACOTTA);
        addMapping("赤色のテラコッタ", Material.RED_TERRACOTTA);
        addMapping("黒色のテラコッタ", Material.BLACK_TERRACOTTA);
        
        addMapping("白色のシュルカーボックス", Material.WHITE_SHULKER_BOX);
        addMapping("橙色のシュルカーボックス", Material.ORANGE_SHULKER_BOX);
        addMapping("赤紫色のシュルカーボックス", Material.MAGENTA_SHULKER_BOX);
        addMapping("空色のシュルカーボックス", Material.LIGHT_BLUE_SHULKER_BOX);
        addMapping("黄色のシュルカーボックス", Material.YELLOW_SHULKER_BOX);
        addMapping("黄緑色のシュルカーボックス", Material.LIME_SHULKER_BOX);
        addMapping("桃色のシュルカーボックス", Material.PINK_SHULKER_BOX);
        addMapping("灰色のシュルカーボックス", Material.GRAY_SHULKER_BOX);
        addMapping("薄灰色のシュルカーボックス", Material.LIGHT_GRAY_SHULKER_BOX);
        addMapping("青緑色のシュルカーボックス", Material.CYAN_SHULKER_BOX);
        addMapping("紫色のシュルカーボックス", Material.PURPLE_SHULKER_BOX);
        addMapping("青色のシュルカーボックス", Material.BLUE_SHULKER_BOX);
        addMapping("茶色のシュルカーボックス", Material.BROWN_SHULKER_BOX);
        addMapping("緑色のシュルカーボックス", Material.GREEN_SHULKER_BOX);
        addMapping("赤色のシュルカーボックス", Material.RED_SHULKER_BOX);
        addMapping("黒色のシュルカーボックス", Material.BLACK_SHULKER_BOX);
        
        addMapping("鉄の剣", Material.IRON_SWORD);
        addMapping("金の剣", Material.GOLDEN_SWORD);
        addMapping("ダイヤモンドの剣", Material.DIAMOND_SWORD);
        addMapping("ネザライトの剣", Material.NETHERITE_SWORD);
        addMapping("木の剣", Material.WOODEN_SWORD);
        addMapping("石の剣", Material.STONE_SWORD);
        
        addMapping("鉄の斧", Material.IRON_AXE);
        addMapping("金の斧", Material.GOLDEN_AXE);
        addMapping("ダイヤモンドの斧", Material.DIAMOND_AXE);
        addMapping("ネザライトの斧", Material.NETHERITE_AXE);
        addMapping("木の斧", Material.WOODEN_AXE);
        addMapping("石の斧", Material.STONE_AXE);
        
        addMapping("鉄のツルハシ", Material.IRON_PICKAXE);
        addMapping("金のツルハシ", Material.GOLDEN_PICKAXE);
        addMapping("ダイヤモンドのツルハシ", Material.DIAMOND_PICKAXE);
        addMapping("ネザライトのツルハシ", Material.NETHERITE_PICKAXE);
        addMapping("木のツルハシ", Material.WOODEN_PICKAXE);
        addMapping("石のツルハシ", Material.STONE_PICKAXE);
        
        addMapping("鉄のシャベル", Material.IRON_SHOVEL);
        addMapping("金のシャベル", Material.GOLDEN_SHOVEL);
        addMapping("ダイヤモンドのシャベル", Material.DIAMOND_SHOVEL);
        addMapping("ネザライトのシャベル", Material.NETHERITE_SHOVEL);
        addMapping("木のシャベル", Material.WOODEN_SHOVEL);
        addMapping("石のシャベル", Material.STONE_SHOVEL);
        
        addMapping("鉄のクワ", Material.IRON_HOE);
        addMapping("金のクワ", Material.GOLDEN_HOE);
        addMapping("ダイヤモンドのクワ", Material.DIAMOND_HOE);
        addMapping("ネザライトのクワ", Material.NETHERITE_HOE);
        addMapping("木のクワ", Material.WOODEN_HOE);
        addMapping("石のクワ", Material.STONE_HOE);
        
        addMapping("鉄のヘルメット", Material.IRON_HELMET);
        addMapping("金のヘルメット", Material.GOLDEN_HELMET);
        addMapping("ダイヤモンドのヘルメット", Material.DIAMOND_HELMET);
        addMapping("ネザライトのヘルメット", Material.NETHERITE_HELMET);
        addMapping("革の帽子", Material.LEATHER_HELMET);
        addMapping("鎖のヘルメット", Material.CHAINMAIL_HELMET);
        
        addMapping("鉄のチェストプレート", Material.IRON_CHESTPLATE);
        addMapping("金のチェストプレート", Material.GOLDEN_CHESTPLATE);
        addMapping("ダイヤモンドのチェストプレート", Material.DIAMOND_CHESTPLATE);
        addMapping("ネザライトのチェストプレート", Material.NETHERITE_CHESTPLATE);
        addMapping("革のチュニック", Material.LEATHER_CHESTPLATE);
        addMapping("鎖のチェストプレート", Material.CHAINMAIL_CHESTPLATE);
        
        addMapping("鉄のレギンス", Material.IRON_LEGGINGS);
        addMapping("金のレギンス", Material.GOLDEN_LEGGINGS);
        addMapping("ダイヤモンドのレギンス", Material.DIAMOND_LEGGINGS);
        addMapping("ネザライトのレギンス", Material.NETHERITE_LEGGINGS);
        addMapping("革のズボン", Material.LEATHER_LEGGINGS);
        addMapping("鎖のレギンス", Material.CHAINMAIL_LEGGINGS);
        
        addMapping("鉄のブーツ", Material.IRON_BOOTS);
        addMapping("金のブーツ", Material.GOLDEN_BOOTS);
        addMapping("ダイヤモンドのブーツ", Material.DIAMOND_BOOTS);
        addMapping("ネザライトのブーツ", Material.NETHERITE_BOOTS);
        addMapping("革のブーツ", Material.LEATHER_BOOTS);
        addMapping("鎖のブーツ", Material.CHAINMAIL_BOOTS);
        
        addMapping("弓", Material.BOW);
        addMapping("クロスボウ", Material.CROSSBOW);
        
        addMapping("釣竿", Material.FISHING_ROD);
        addMapping("ハサミ", Material.SHEARS);
        addMapping("盾", Material.SHIELD);
        addMapping(" Flint and Steel", Material.FLINT_AND_STEEL);
        addMapping("火打石と打ち金", Material.FLINT_AND_STEEL);
        
        addMapping("本", Material.BOOK);
        addMapping("本と羽根ペン", Material.WRITABLE_BOOK);
        addMapping("記入済みの本", Material.WRITTEN_BOOK);
        addMapping("エンチャントの本", Material.ENCHANTED_BOOK);
        
        addMapping("地図", Material.MAP);
        addMapping("コンパス", Material.COMPASS);
        addMapping("時計", Material.CLOCK);
        
        addMapping("松明", Material.TORCH);
        addMapping("魂の松明", Material.SOUL_TORCH);
        addMapping("ランタン", Material.LANTERN);
        addMapping("魂のランタン", Material.SOUL_LANTERN);
        addMapping("グロウストーン", Material.GLOWSTONE);
        addMapping("シーランタン", Material.SEA_LANTERN);
        addMapping("エンドロッド", Material.END_ROD);
        
        addMapping("かまど", Material.FURNACE);
        addMapping("溶鉱炉", Material.BLAST_FURNACE);
        addMapping("燻製器", Material.SMOKER);
        addMapping("醸造台", Material.BREWING_STAND);
        addMapping("金床", Material.ANVIL);
        
        addMapping("レール", Material.RAIL);
        addMapping("パワードレール", Material.POWERED_RAIL);
        addMapping("ディテクターレール", Material.DETECTOR_RAIL);
        addMapping("アクチベーターレール", Material.ACTIVATOR_RAIL);
        
        addMapping("トロッコ", Material.MINECART);
        addMapping("チェスト付きトロッコ", Material.CHEST_MINECART);
        addMapping("かまど付きトロッコ", Material.FURNACE_MINECART);
        addMapping("ホッパー付きトロッコ", Material.HOPPER_MINECART);
        addMapping("TNT付きトロッコ", Material.TNT_MINECART);
        
        addMapping("レッドストーンブロック", Material.REDSTONE_BLOCK);
        addMapping("レッドストーントーチ", Material.REDSTONE_TORCH);
        addMapping("レバー", Material.LEVER);
        addMapping("ボタン", Material.STONE_BUTTON);
        addMapping("感圧板", Material.STONE_PRESSURE_PLATE);
        addMapping("オブザーバー", Material.OBSERVER);
        addMapping("ピストン", Material.PISTON);
        addMapping("粘着ピストン", Material.STICKY_PISTON);
        addMapping("ディスペンサー", Material.DISPENSER);
        addMapping("ドロッパー", Material.DROPPER);
        addMapping("ホッパー", Material.HOPPER);
        addMapping("コンパレーター", Material.COMPARATOR);
        addMapping("リピーター", Material.REPEATER);
        addMapping("ターゲット", Material.TARGET);
        addMapping("日照センサー", Material.DAYLIGHT_DETECTOR);
        addMapping("トリップワイヤーフック", Material.TRIPWIRE_HOOK);
        
        addMapping("TNT", Material.TNT);
        
        addMapping("白色のベッド", Material.WHITE_BED);
        addMapping("橙色のベッド", Material.ORANGE_BED);
        addMapping("赤紫色のベッド", Material.MAGENTA_BED);
        addMapping("空色のベッド", Material.LIGHT_BLUE_BED);
        addMapping("黄色のベッド", Material.YELLOW_BED);
        addMapping("黄緑色のベッド", Material.LIME_BED);
        addMapping("桃色のベッド", Material.PINK_BED);
        addMapping("灰色のベッド", Material.GRAY_BED);
        addMapping("薄灰色のベッド", Material.LIGHT_GRAY_BED);
        addMapping("青緑色のベッド", Material.CYAN_BED);
        addMapping("紫色のベッド", Material.PURPLE_BED);
        addMapping("青色のベッド", Material.BLUE_BED);
        addMapping("茶色のベッド", Material.BROWN_BED);
        addMapping("緑色のベッド", Material.GREEN_BED);
        addMapping("赤色のベッド", Material.RED_BED);
        addMapping("黒色のベッド", Material.BLACK_BED);
        
        addMapping("白い染料", Material.WHITE_DYE);
        addMapping("橙色の染料", Material.ORANGE_DYE);
        addMapping("赤紫色の染料", Material.MAGENTA_DYE);
        addMapping("空色の染料", Material.LIGHT_BLUE_DYE);
        addMapping("黄色の染料", Material.YELLOW_DYE);
        addMapping("黄緑色の染料", Material.LIME_DYE);
        addMapping("桃色の染料", Material.PINK_DYE);
        addMapping("灰色の染料", Material.GRAY_DYE);
        addMapping("薄灰色の染料", Material.LIGHT_GRAY_DYE);
        addMapping("青緑色の染料", Material.CYAN_DYE);
        addMapping("紫色の染料", Material.PURPLE_DYE);
        addMapping("青色の染料", Material.BLUE_DYE);
        addMapping("茶色の染料", Material.BROWN_DYE);
        addMapping("緑色の染料", Material.GREEN_DYE);
        addMapping("赤色の染料", Material.RED_DYE);
        addMapping("黒色の染料", Material.BLACK_DYE);
        
        addMapping("花火", Material.FIREWORK_ROCKET);
        addMapping("花火の星", Material.FIREWORK_STAR);
        
        addMapping("ポーション", Material.POTION);
        addMapping("スプラッシュポーション", Material.SPLASH_POTION);
        addMapping("残留ポーション", Material.LINGERING_POTION);
        
        addMapping("スケルトンの頭蓋", Material.SKELETON_SKULL);
        addMapping("ウィザースケルトンの頭蓋", Material.WITHER_SKELETON_SKULL);
        addMapping("ゾンビの頭", Material.ZOMBIE_HEAD);
        addMapping("プレイヤーの頭", Material.PLAYER_HEAD);
        addMapping("クリーパーの頭", Material.CREEPER_HEAD);
        addMapping("ドラゴンの頭", Material.DRAGON_HEAD);
        
        addMapping("音楽ディスク", Material.MUSIC_DISC_13);
        
        addMapping("体験瓶", Material.EXPERIENCE_BOTTLE);
        addMapping("エンチャント瓶", Material.EXPERIENCE_BOTTLE);
        
        addMapping("ネザライトの欠片", Material.NETHERITE_SCRAP);
        addMapping("ネザライトインゴット", Material.NETHERITE_INGOT);
        
        addMapping("ピグリンの鼻", Material.PIGLIN_HEAD);
        
        addMapping("銅インゴット", Material.COPPER_INGOT);
        addMapping("銅のブロック", Material.COPPER_BLOCK);
        addMapping("切断された銅", Material.CUT_COPPER);
        
        addMapping("アメジストの欠片", Material.AMETHYST_SHARD);
        addMapping("アメジストのブロック", Material.AMETHYST_BLOCK);
        
        addMapping("スパイグラス", Material.SPYGLASS);
        
        addMapping("蝋燭", Material.CANDLE);
        
        addMapping("マングローブの種", Material.MANGROVE_PROPAGULE);
        
        addMapping("泥", Material.MUD);
        addMapping("泥付きマングローブの根", Material.MUDDY_MANGROVE_ROOTS);
        
        addMapping("竹", Material.BAMBOO);
        addMapping("竹のブロック", Material.BAMBOO_BLOCK);
        addMapping("竹の板材", Material.BAMBOO_PLANKS);
        
        addMapping("サクラの板材", Material.CHERRY_PLANKS);
        addMapping("サクラの原木", Material.CHERRY_LOG);
        addMapping("サクラの葉", Material.CHERRY_LEAVES);
        addMapping("サクラの苗木", Material.CHERRY_SAPLING);
        
        addMapping("飾り鉢", Material.DECORATED_POT);
        
        addMapping("スニッファーの卵", Material.SNIFFER_EGG);
        
        addMapping("たいまつ花", Material.TORCHFLOWER);
        addMapping("たいまつ花の種", Material.TORCHFLOWER_SEEDS);
        
        addMapping("ピッチャーポッド", Material.PITCHER_POD);
        addMapping("ピッチャー植物", Material.PITCHER_PLANT);
    }
    
    private static void addMapping(String japaneseName, Material material) {
        JAPANESE_TO_MATERIAL.put(japaneseName, material);
        MATERIAL_TO_JAPANESE.put(material, japaneseName);
    }
    
    public static Material fromJapaneseName(String japaneseName) {
        return JAPANESE_TO_MATERIAL.get(japaneseName);
    }
    
    public static String toJapaneseName(Material material) {
        return MATERIAL_TO_JAPANESE.get(material);
    }
    
    public static boolean isJapaneseName(String input) {
        return JAPANESE_TO_MATERIAL.containsKey(input);
    }
    
    public static java.util.List<String> getAllJapaneseNames() {
        return new java.util.ArrayList<>(JAPANESE_TO_MATERIAL.keySet());
    }
    
    public static java.util.List<String> getJapaneseNamesStartingWith(String prefix) {
        java.util.List<String> results = new java.util.ArrayList<>();
        for (String name : JAPANESE_TO_MATERIAL.keySet()) {
            if (name.startsWith(prefix)) {
                results.add(name);
            }
        }
        java.util.Collections.sort(results);
        return results;
    }
}
