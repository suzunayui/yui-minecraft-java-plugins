package com.suzunayui.containersearch;

import org.bukkit.plugin.java.JavaPlugin;

public class ContainerSearch extends JavaPlugin {
    
    private static ContainerSearch instance;
    
    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        
        if (getCommand("containersearch") != null) {
            ContainerSearchCommand executor = new ContainerSearchCommand(this);
            getCommand("containersearch").setExecutor(executor);
            getCommand("containersearch").setTabCompleter(executor);
        }
        
        getLogger().info("ContainerSearch has been enabled!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("ContainerSearch has been disabled!");
    }
    
    public static ContainerSearch getInstance() {
        return instance;
    }
}
