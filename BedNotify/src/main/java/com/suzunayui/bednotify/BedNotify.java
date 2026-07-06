package com.suzunayui.bednotify;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BedNotify extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("BedNotify has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("BedNotify has been disabled!");
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            return;
        }

        Player player = event.getPlayer();
        String message = "§e" + player.getName() + "さんがベッドで寝ました。";

        for (Player onlinePlayer : getServer().getOnlinePlayers()) {
            onlinePlayer.sendMessage(message);
        }
    }
}
