package com.suzunayui.personalkeepinventory;

import org.bukkit.plugin.java.JavaPlugin;

public class PersonalKeepInventory extends JavaPlugin {

    private static PersonalKeepInventory instance;
    private KeepManager keepManager;

    @Override
    public void onEnable() {
        instance = this;

        keepManager = new KeepManager(this);
        keepManager.loadData();

        if (getCommand("keep") != null) {
            getCommand("keep").setExecutor(new KeepCommand(this));
        }

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        getLogger().info("PersonalKeepInventory has been enabled!");
    }

    @Override
    public void onDisable() {
        keepManager.saveData();
        getLogger().info("PersonalKeepInventory has been disabled!");
    }

    public KeepManager getKeepManager() {
        return keepManager;
    }

    public static PersonalKeepInventory getInstance() {
        return instance;
    }
}
