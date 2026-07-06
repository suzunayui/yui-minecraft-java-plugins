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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> createBackup());
        
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
                    Bukkit.getScheduler().runTaskAsynchronously(instance, () -> createBackup());
                }
            }
        };
        
        long oneHourTicks = 20L * 60 * 60;
        backupTask.runTaskTimer(this, oneHourTicks, oneHourTicks);
    }
    
    public void createBackup() {
        String timestamp = LocalDateTime.now(ZoneId.of("Asia/Tokyo")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));
        File backupFolder = new File(backupDir, timestamp);
        File zipFile = new File(backupDir, timestamp + ".zip");
        
        try {
            for (World world : Bukkit.getWorlds()) {
                File worldFolder = world.getWorldFolder();
                Path targetPath = backupFolder.toPath().resolve(worldFolder.getName());
                copyFolder(worldFolder.toPath(), targetPath);
            }
            
            getLogger().info("Copying completed. Compressing to zip...");
            compressToZip(backupFolder, zipFile);
            
            boolean deleteAfterCompress = getConfig().getBoolean("delete-after-compress", true);
            if (deleteAfterCompress) {
                deleteFolder(backupFolder);
            }
            
            getLogger().info("Backup created: " + zipFile.getName());
        } catch (IOException e) {
            getLogger().severe("Failed to create backup: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void copyFolder(Path source, Path target) throws IOException {
        Files.createDirectories(target);
        
        File[] files = source.toFile().listFiles();
        if (files == null) return;
        
        for (File file : files) {
            Path sourcePath = source.resolve(file.getName());
            Path targetPath = target.resolve(file.getName());
            
            if (file.isDirectory()) {
                copyFolder(sourcePath, targetPath);
            } else {
                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
    
    private void compressToZip(File sourceFolder, File zipFile) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            zipFolder(sourceFolder, sourceFolder.getName(), zos);
        }
    }
    
    private void zipFolder(File folder, String parentPath, ZipOutputStream zos) throws IOException {
        File[] files = folder.listFiles();
        if (files == null) return;
        
        byte[] buffer = new byte[8192];
        
        for (File file : files) {
            String entryPath = parentPath + "/" + file.getName();
            
            if (file.isDirectory()) {
                zipFolder(file, entryPath, zos);
            } else {
                ZipEntry entry = new ZipEntry(entryPath);
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
    
    private void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isDirectory()) {
                deleteFolder(file);
            } else {
                file.delete();
            }
        }
        folder.delete();
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
