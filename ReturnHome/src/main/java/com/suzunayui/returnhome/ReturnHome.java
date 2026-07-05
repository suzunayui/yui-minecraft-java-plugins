package com.suzunayui.returnhome;

import org.bukkit.plugin.java.JavaPlugin;

public class ReturnHome extends JavaPlugin {
    
    private static ReturnHome instance;
    private HomeManager homeManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        
        homeManager = new HomeManager(this);
        
        if (getCommand("home") != null) {
            getCommand("home").setExecutor(new HomeCommand(this));
        }
        
        getLogger().info("ReturnHome has been enabled!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("ReturnHome has been disabled!");
    }
    
    public HomeManager getHomeManager() {
        return homeManager;
    }
    
    public static ReturnHome getInstance() {
        return instance;
    }
}
