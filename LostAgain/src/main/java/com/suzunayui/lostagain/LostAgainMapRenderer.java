package com.suzunayui.lostagain;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.Color;
import java.util.UUID;

public class LostAgainMapRenderer extends MapRenderer {

    private final UUID playerUUID;

    // 小さいほど細かい。まずは8がおすすめ
    private static final int SCALE = 8;

    // render() が何度も呼ばれるので、1回だけ描画する
    private boolean rendered = false;

    public LostAgainMapRenderer(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    @Override
    public void render(MapView mapView, MapCanvas canvas, Player player) {
        if (!player.getUniqueId().equals(playerUUID)) {
            return;
        }

        if (rendered) {
            return;
        }

        rendered = true;

        int centerX = player.getLocation().getBlockX();
        int centerZ = player.getLocation().getBlockZ();
        World world = player.getWorld();

        for (int mapX = 0; mapX < 128; mapX++) {
            for (int mapZ = 0; mapZ < 128; mapZ++) {

                int blockX = centerX + (mapX - 64) * SCALE;
                int blockZ = centerZ + (mapZ - 64) * SCALE;

                int chunkX = blockX >> 4;
                int chunkZ = blockZ >> 4;

                // 未ロードチャンクは灰色
                if (!world.isChunkLoaded(chunkX, chunkZ)) {
                    canvas.setPixelColor(mapX, mapZ, new Color(125, 125, 125));
                    continue;
                }

                int highestY = world.getHighestBlockYAt(blockX, blockZ);
                Block block = world.getBlockAt(blockX, highestY, blockZ);

                canvas.setPixelColor(mapX, mapZ, getBlockColor(block.getType()));
            }
        }
    }

    private Color getBlockColor(Material material) {
        return switch (material) {
            case GRASS_BLOCK, SHORT_GRASS, TALL_GRASS, FERN, LARGE_FERN ->
                    new Color(124, 164, 70);

            case DIRT, COARSE_DIRT, ROOTED_DIRT ->
                    new Color(134, 96, 67);

            case STONE, COBBLESTONE, MOSSY_COBBLESTONE ->
                    new Color(125, 125, 125);

            case SAND, RED_SAND, SANDSTONE, RED_SANDSTONE ->
                    new Color(219, 211, 160);

            case WATER ->
                    new Color(62, 91, 179);

            case OAK_LOG, BIRCH_LOG, SPRUCE_LOG, JUNGLE_LOG, ACACIA_LOG, DARK_OAK_LOG,
                 MANGROVE_LOG, CHERRY_LOG ->
                    new Color(106, 82, 54);

            case OAK_LEAVES, BIRCH_LEAVES, SPRUCE_LEAVES, JUNGLE_LEAVES, ACACIA_LEAVES,
                 DARK_OAK_LEAVES, MANGROVE_LEAVES, CHERRY_LEAVES ->
                    new Color(76, 127, 46);

            case SNOW, SNOW_BLOCK ->
                    new Color(249, 254, 254);

            case ICE, PACKED_ICE, BLUE_ICE ->
                    new Color(127, 167, 255);

            case CLAY ->
                    new Color(161, 167, 174);

            case GRAVEL ->
                    new Color(131, 127, 126);

            case OAK_PLANKS, BIRCH_PLANKS, SPRUCE_PLANKS, JUNGLE_PLANKS, ACACIA_PLANKS,
                 DARK_OAK_PLANKS, MANGROVE_PLANKS, CHERRY_PLANKS ->
                    new Color(162, 132, 82);

            case COBBLESTONE_WALL, MOSSY_COBBLESTONE_WALL ->
                    new Color(125, 125, 125);

            case BRICKS ->
                    new Color(155, 101, 82);

            case NETHERRACK ->
                    new Color(97, 49, 51);

            case SOUL_SAND, SOUL_SOIL ->
                    new Color(74, 59, 44);

            case OBSIDIAN, CRYING_OBSIDIAN ->
                    new Color(15, 10, 26);

            case BEDROCK ->
                    new Color(85, 85, 85);

            case MYCELIUM ->
                    new Color(103, 77, 97);

            case PODZOL ->
                    new Color(81, 64, 42);

            case TERRACOTTA ->
                    new Color(152, 94, 67);

            case RED_TERRACOTTA ->
                    new Color(143, 61, 47);

            case ORANGE_TERRACOTTA ->
                    new Color(161, 83, 37);

            case YELLOW_TERRACOTTA ->
                    new Color(186, 133, 36);

            case WHITE_TERRACOTTA ->
                    new Color(210, 178, 161);

            case LIGHT_GRAY_TERRACOTTA ->
                    new Color(135, 107, 98);

            case BROWN_TERRACOTTA ->
                    new Color(92, 64, 51);

            case DEEPSLATE, TUFF ->
                    new Color(80, 80, 80);

            case ANDESITE ->
                    new Color(136, 136, 136);

            case DIORITE, CALCITE ->
                    new Color(220, 220, 220);

            case GRANITE ->
                    new Color(149, 103, 85);

            default ->
                    new Color(125, 125, 125);
        };
    }
}