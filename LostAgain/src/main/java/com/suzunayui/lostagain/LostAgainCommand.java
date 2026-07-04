package com.suzunayui.lostagain;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import java.util.ArrayList;
import java.util.List;

public class LostAgainCommand implements CommandExecutor {

    private final LostAgain plugin;

    public LostAgainCommand(LostAgain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("\u00a7c\u3053\u306e\u30b3\u30de\u30f3\u30c9\u306f\u30d7\u30ec\u30a4\u30e4\u30fc\u306e\u307f\u4f7f\u7528\u3067\u304d\u307e\u3059\u3002");
            return true;
        }

        MapView mapView = Bukkit.createMap(player.getWorld());
        mapView.setCenterX(player.getLocation().getBlockX());
        mapView.setCenterZ(player.getLocation().getBlockZ());
        mapView.setScale(MapView.Scale.FAR);

        ItemStack mapItem = new ItemStack(Material.FILLED_MAP);
        MapMeta mapMeta = (MapMeta) mapItem.getItemMeta();
        mapMeta.setMapView(mapView);

        mapMeta.displayName(Component.text("\u8ff7\u5b50\u5bfe\u7b56\u306e\u5730\u56f3")
            .color(NamedTextColor.GOLD)
            .decoration(TextDecoration.ITALIC, false));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text("\u53f3\u30af\u30ea\u30c3\u30af\u3067\u81ea\u5206\u4e2d\u5fc3\u306b\u66f4\u65b0")
            .color(NamedTextColor.GRAY)
            .decoration(TextDecoration.ITALIC, false));
        mapMeta.lore(lore);

        mapItem.setItemMeta(mapMeta);

        player.getInventory().addItem(mapItem);
        player.sendMessage("\u00a7a\u8ff7\u5b50\u5bfe\u7b56\u306e\u5730\u56f3\u3092\u5165\u624b\u3057\u307e\u3057\u305f\uff01");

        return true;
    }
}
