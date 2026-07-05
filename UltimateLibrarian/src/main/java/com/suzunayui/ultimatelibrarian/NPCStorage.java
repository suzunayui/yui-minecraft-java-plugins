package com.suzunayui.ultimatelibrarian;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NPCStorage {
    
    private final UltimateLibrarian plugin;
    private File dataFile;
    private FileConfiguration data;
    
    public NPCStorage(UltimateLibrarian plugin) {
        this.plugin = plugin;
    }
    
    public void load() {
        dataFile = new File(plugin.getDataFolder(), "npcs.yml");
        if (!dataFile.exists()) {
            data = new YamlConfiguration();
            return;
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
    }
    
    public void save() {
        try {
            data.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save NPC data: " + e.getMessage());
        }
    }
    
    public void addNPC(Location location, String type) {
        String key = generateKey(location);
        data.set("npcs." + key + ".world", location.getWorld().getName());
        data.set("npcs." + key + ".x", location.getBlockX());
        data.set("npcs." + key + ".y", location.getBlockY());
        data.set("npcs." + key + ".z", location.getBlockZ());
        data.set("npcs." + key + ".type", type);
        save();
    }
    
    public void removeNPC(Location location) {
        String key = generateKey(location);
        data.set("npcs." + key, null);
        save();
    }
    
    public void removeAll() {
        data.set("npcs", null);
        save();
    }
    
    public List<NPCData> getAllNPCs() {
        List<NPCData> npcs = new ArrayList<>();
        ConfigurationSection section = data.getConfigurationSection("npcs");
        if (section == null) return npcs;
        
        for (String key : section.getKeys(false)) {
            String worldName = section.getString(key + ".world");
            int x = section.getInt(key + ".x");
            int y = section.getInt(key + ".y");
            int z = section.getInt(key + ".z");
            String type = section.getString(key + ".type");
            
            World world = Bukkit.getWorld(worldName);
            if (world != null && type != null) {
                Location loc = new Location(world, x, y, z);
                npcs.add(new NPCData(loc, type));
            }
        }
        return npcs;
    }
    
    public List<NPCData> getNPCsInChunk(int chunkX, int chunkZ, World world) {
        List<NPCData> npcs = new ArrayList<>();
        for (NPCData npc : getAllNPCs()) {
            if (npc.location().getWorld() == world &&
                npc.location().getBlockX() >> 4 == chunkX &&
                npc.location().getBlockZ() >> 4 == chunkZ) {
                npcs.add(npc);
            }
        }
        return npcs;
    }
    
    private String generateKey(Location location) {
        return location.getWorld().getName() + "_" + 
               location.getBlockX() + "_" + 
               location.getBlockY() + "_" + 
               location.getBlockZ();
    }
    
    public record NPCData(Location location, String type) {}
}
