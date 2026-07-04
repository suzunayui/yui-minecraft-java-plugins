package com.suzunayui.lostagain;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.util.ArrayList;

public class MapListener implements Listener {

    private static final String MAP_NAME = "\u8ff7\u5b50\u5bfe\u7b56\u306e\u5730\u56f3";
    private final LostAgain plugin;

    public MapListener(LostAgain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT_CLICK")) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() != Material.FILLED_MAP || !item.hasItemMeta()) {
            return;
        }

        MapMeta meta = (MapMeta) item.getItemMeta();
        if (!meta.hasDisplayName()) {
            return;
        }

        String displayName = PlainTextComponentSerializer.plainText().serialize(meta.displayName());
        if (!MAP_NAME.equals(displayName)) {
            return;
        }

        MapView mapView = meta.getMapView();
        if (mapView == null) {
            return;
        }

        mapView.setCenterX(player.getLocation().getBlockX());
        mapView.setCenterZ(player.getLocation().getBlockZ());

        for (MapRenderer renderer : new ArrayList<>(mapView.getRenderers())) {
            mapView.removeRenderer(renderer);
        }
        mapView.addRenderer(new LostAgainMapRenderer(player.getUniqueId()));

        player.sendMap(mapView);

        player.sendActionBar(Component.text("\u5730\u56f3\u3092\u66f4\u65b0\u3057\u307e\u3057\u305f")
            .color(NamedTextColor.GREEN));
    }
}
