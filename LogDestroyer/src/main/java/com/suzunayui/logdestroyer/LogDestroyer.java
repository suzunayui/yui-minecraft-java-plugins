package com.suzunayui.logdestroyer;

import org.bukkit.plugin.java.JavaPlugin;

public class LogDestroyer extends JavaPlugin {
    
    private static LogDestroyer instance;
    
    @Override
    public void onEnable() {
        instance = this;
        
        getServer().getPluginManager().registerEvents(new LogDestroyListener(), this);
        
        getLogger().info("LogDestroyer has been enabled!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("LogDestroyer has been disabled!");
    }
    
    public static LogDestroyer getInstance() {
        return instance;
    }
}
