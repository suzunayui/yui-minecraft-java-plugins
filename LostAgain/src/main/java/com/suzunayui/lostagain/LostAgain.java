package com.suzunayui.lostagain;

import org.bukkit.plugin.java.JavaPlugin;

public class LostAgain extends JavaPlugin {

    private static LostAgain instance;

    @Override
    public void onEnable() {
        instance = this;

        if (getCommand("lostagain") != null) {
            LostAgainCommand executor = new LostAgainCommand(this);
            getCommand("lostagain").setExecutor(executor);
        }

        getServer().getPluginManager().registerEvents(new MapListener(this), this);

        getLogger().info("LostAgain has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("LostAgain has been disabled!");
    }

    public static LostAgain getInstance() {
        return instance;
    }
}
