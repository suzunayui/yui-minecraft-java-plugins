package com.suzunayui.personalkeepinventory;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KeepManager {

    private final PersonalKeepInventory plugin;
    private final Map<UUID, Boolean> keepInventoryMap = new HashMap<>();

    public KeepManager(PersonalKeepInventory plugin) {
        this.plugin = plugin;
    }

    public void loadData() {
        File file = new File(plugin.getDataFolder(), "keepdata.yml");
        if (!file.exists()) {
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (config.getConfigurationSection("players") == null) {
            return;
        }

        for (String key : config.getConfigurationSection("players").getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                boolean value = config.getBoolean("players." + key);
                keepInventoryMap.put(uuid, value);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public void saveData() {
        File file = new File(plugin.getDataFolder(), "keepdata.yml");
        YamlConfiguration config = new YamlConfiguration();

        for (Map.Entry<UUID, Boolean> entry : keepInventoryMap.entrySet()) {
            config.set("players." + entry.getKey().toString(), entry.getValue());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save keep data: " + e.getMessage());
        }
    }

    public boolean isKeepInventory(UUID uuid) {
        return keepInventoryMap.getOrDefault(uuid, false);
    }

    public void toggleKeepInventory(UUID uuid) {
        boolean current = isKeepInventory(uuid);
        keepInventoryMap.put(uuid, !current);
    }

    public void setKeepInventory(UUID uuid, boolean value) {
        keepInventoryMap.put(uuid, value);
    }
}
