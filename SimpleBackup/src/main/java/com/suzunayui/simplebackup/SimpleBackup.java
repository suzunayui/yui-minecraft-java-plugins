package com.suzunayui.simplebackup;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class SimpleBackup extends JavaPlugin {
    
    private static SimpleBackup instance;
    private BukkitRunnable backupTask;
    private File backupDir;
    
    @Override
    public void onEnable() {
        instance = this;
        
        backupDir = new File(getDataFolder(), "backups");
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
        
        getLogger().info("Starting backup...");
        createBackup();
        
        startScheduledBackup();
        
        getLogger().info("SimpleBackup has been enabled!");
    }
    
    @Override
    public void onDisable() {
        if (backupTask != null) {
            backupTask.cancel();
        }
        getLogger().info("SimpleBackup has been disabled!");
    }
    
    private void startScheduledBackup() {
        backupTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().size() >= 1) {
                    getLogger().info("Creating scheduled backup...");
                    createBackup();
                }
            }
        };
        
        long oneHourTicks = 20L * 60 * 60;
        backupTask.runTaskTimer(this, oneHourTicks, oneHourTicks);
    }
    
    public void createBackup() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));
        File backupFile = new File(backupDir, timestamp + ".zip");
        
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(backupFile))) {
            for (World world : Bukkit.getWorlds()) {
                File worldFolder = world.getWorldFolder();
                addFolderToZip(zos, worldFolder, worldFolder.getName());
            }
            
            getLogger().info("Backup created: " + backupFile.getName());
        } catch (IOException e) {
            getLogger().severe("Failed to create backup: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void addFolderToZip(ZipOutputStream zos, File folder, String parentPath) throws IOException {
        File[] files = folder.listFiles();
        if (files == null) return;
        
        byte[] buffer = new byte[8192];
        
        for (File file : files) {
            String filePath = parentPath + "/" + file.getName();
            
            if (file.isDirectory()) {
                addFolderToZip(zos, file, filePath);
            } else {
                ZipEntry entry = new ZipEntry(filePath);
                zos.putNextEntry(entry);
                
                try (FileInputStream fis = new FileInputStream(file)) {
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                }
                
                zos.closeEntry();
            }
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("simplebackup.admin")) {
            sender.sendMessage("§c権限がありません。");
            return true;
        }
        
        sender.sendMessage("§aバックアップを作成中...");
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            createBackup();
            Bukkit.getScheduler().runTask(this, () -> {
                sender.sendMessage("§aバックアップが完了しました。");
            });
        });
        
        return true;
    }
    
    public static SimpleBackup getInstance() {
        return instance;
    }
}
