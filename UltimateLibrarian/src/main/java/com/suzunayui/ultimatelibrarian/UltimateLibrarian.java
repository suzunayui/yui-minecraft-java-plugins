package com.suzunayui.ultimatelibrarian;

import org.bukkit.plugin.java.JavaPlugin;

public class UltimateLibrarian extends JavaPlugin {
    
    private static UltimateLibrarian instance;
    private LibrarianManager librarianManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        librarianManager = new LibrarianManager(this);
        
        if (getCommand("ultimatelibrarian") != null) {
            LibrarianCommand cmd = new LibrarianCommand(this);
            getCommand("ultimatelibrarian").setExecutor(cmd);
            getCommand("ultimatelibrarian").setTabCompleter(cmd);
        }
        
        getLogger().info("UltimateLibrarian has been enabled!");
    }
    
    @Override
    public void onDisable() {
        if (librarianManager != null) {
            librarianManager.removeAllNPCs();
        }
        getLogger().info("UltimateLibrarian has been disabled!");
    }
    
    public LibrarianManager getLibrarianManager() {
        return librarianManager;
    }
    
    public static UltimateLibrarian getInstance() {
        return instance;
    }
}
