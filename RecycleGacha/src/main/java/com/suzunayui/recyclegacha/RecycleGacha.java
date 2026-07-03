package com.suzunayui.recyclegacha;

import org.bukkit.plugin.java.JavaPlugin;

public class RecycleGacha extends JavaPlugin {
    
    private static RecycleGacha instance;
    private GachaManager gachaManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        
        gachaManager = new GachaManager(this);
        
        getServer().getPluginManager().registerEvents(new GachaListener(this), this);
        
        if (getCommand("recyclegacha") != null) {
            getCommand("recyclegacha").setExecutor(new GachaCommand(this));
        }
        
        getLogger().info("RecycleGacha has been enabled!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("RecycleGacha has been disabled!");
    }
    
    public GachaManager getGachaManager() {
        return gachaManager;
    }
    
    public static RecycleGacha getInstance() {
        return instance;
    }
}
