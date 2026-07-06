package com.suzunayui.tinyprotect;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TinyProtect extends JavaPlugin {

    private static TinyProtect instance;
    private DatabaseManager databaseManager;
    private final Set<UUID> inspectModePlayers = new HashSet<>();

    @Override
    public void onEnable() {
        instance = this;

        databaseManager = new DatabaseManager(this);
        databaseManager.connect();

        getServer().getPluginManager().registerEvents(new BlockEventListener(this), this);
        getServer().getPluginManager().registerEvents(new ContainerEventListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerEventListener(this), this);
        getServer().getPluginManager().registerEvents(new InspectListener(this), this);

        CommandExecutor executor = new TinyProtectCommand(this);
        TabCompleter tabCompleter = new TinyProtectTabCompleter();
        var cmd = getCommand("ti");
        if (cmd != null) {
            cmd.setExecutor(executor);
            cmd.setTabCompleter(tabCompleter);
        }

        getLogger().info("TinyProtect has been enabled!");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.disconnect();
        }
        getLogger().info("TinyProtect has been disabled!");
    }

    public static TinyProtect getInstance() {
        return instance;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public boolean isInspectMode(UUID playerId) {
        return inspectModePlayers.contains(playerId);
    }

    public void toggleInspectMode(UUID playerId) {
        if (inspectModePlayers.contains(playerId)) {
            inspectModePlayers.remove(playerId);
        } else {
            inspectModePlayers.add(playerId);
        }
    }
}
