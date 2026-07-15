package com.suzunayui.simplebackup;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class SimpleBackup extends JavaPlugin implements Listener {
    private BukkitRunnable backupTask;
    private File backupDir;
    private final AtomicLong lastBackupTime = new AtomicLong(0);
    private final AtomicBoolean backupRunning = new AtomicBoolean(false);
    private static final long BACKUP_INTERVAL_MS = 60 * 60 * 1000L;
    private static final DateTimeFormatter BACKUP_NAME = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final DateTimeFormatter LEGACY_BACKUP_NAME = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");

    @Override
    public void onEnable() {
        saveDefaultConfig();
        backupDir = new File(getDataFolder(), "backups");
        if (!backupDir.exists() && !backupDir.mkdirs()) {
            getLogger().severe("Could not create the backup directory: " + backupDir);
        }
        getServer().getPluginManager().registerEvents(this, this);
        requestBackup(null);
        startScheduledBackup();
        getLogger().info("SimpleBackup has been enabled!");
    }

    @Override
    public void onDisable() {
        if (backupTask != null) backupTask.cancel();
        getLogger().info("SimpleBackup has been disabled!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (System.currentTimeMillis() - lastBackupTime.get() >= BACKUP_INTERVAL_MS) {
            requestBackup(null);
        }
    }

    private void startScheduledBackup() {
        backupTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!Bukkit.getOnlinePlayers().isEmpty()) requestBackup(null);
            }
        };
        long oneHourTicks = 20L * 60 * 60;
        backupTask.runTaskTimer(this, oneHourTicks, oneHourTicks);
    }

    /** Must be called on the server thread. */
    private void requestBackup(CommandSender requester) {
        if (!backupRunning.compareAndSet(false, true)) {
            if (requester != null) requester.sendMessage("§eバックアップは既に実行中です。");
            return;
        }

        getLogger().info("Saving worlds and creating a consistent backup snapshot...");
        if (requester != null) requester.sendMessage("§aバックアップを作成中です...");

        List<WorldSaveState> worlds = new ArrayList<>();
        List<Path> sources = new ArrayList<>();
        Set<Path> seen = new HashSet<>();
        try {
            // Bukkit world operations are deliberately kept on the server thread.
            // Once each world is flushed, autosave remains disabled until copying ends,
            // so region files cannot change underneath the asynchronous copy.
            for (World world : Bukkit.getWorlds()) {
                world.save();
                worlds.add(new WorldSaveState(world, world.isAutoSave()));
                world.setAutoSave(false);

                Path source = world.getWorldFolder().toPath().toAbsolutePath().normalize();
                if (seen.add(source)) sources.add(source);
            }
            // Paper may expose dimensions below the main world folder. Copying the
            // parent already includes them and preserves their required directory layout.
            List<Path> allSources = List.copyOf(sources);
            sources.removeIf(candidate -> allSources.stream().anyMatch(other ->
                    !candidate.equals(other) && candidate.startsWith(other)));
        } catch (RuntimeException e) {
            restoreAutoSave(worlds);
            backupRunning.set(false);
            getLogger().severe("Failed to prepare backup: " + e.getMessage());
            if (requester != null) requester.sendMessage("§cバックアップの準備に失敗しました。");
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            boolean success = false;
            String error = null;
            try {
                createBackup(sources);
                success = true;
            } catch (IOException e) {
                error = e.getMessage();
                getLogger().severe("Failed to create backup: " + e.getMessage());
            }

            boolean completed = success;
            String failure = error;
            Bukkit.getScheduler().runTask(this, () -> {
                restoreAutoSave(worlds);
                backupRunning.set(false);
                if (completed) {
                    lastBackupTime.set(System.currentTimeMillis());
                    if (requester != null) requester.sendMessage("§aバックアップが完了しました。");
                } else if (requester != null) {
                    requester.sendMessage("§cバックアップに失敗しました: " + failure);
                }
            });
        });
    }

    private void restoreAutoSave(List<WorldSaveState> worlds) {
        for (WorldSaveState state : worlds) {
            if (state.autoSave()) state.world().setAutoSave(true);
        }
    }

    private void createBackup(List<Path> sources) throws IOException {
        String timestamp = LocalDateTime.now(ZoneId.of("Asia/Tokyo")).format(BACKUP_NAME);
        Path staging = backupDir.toPath().resolve(timestamp + ".staging");
        Path incompleteZip = backupDir.toPath().resolve(timestamp + ".zip.part");
        Path finalZip = backupDir.toPath().resolve(timestamp + ".zip");

        try {
            for (Path source : sources) copyFolder(source, staging.resolve(source.getFileName()));
            getLogger().info("Snapshot copied. Compressing to zip...");
            compressToZip(staging, incompleteZip);
            Files.move(incompleteZip, finalZip, StandardCopyOption.ATOMIC_MOVE);
            getLogger().info("Backup created: " + finalZip.getFileName());
            cleanupOldBackups();
        } finally {
            Files.deleteIfExists(incompleteZip);
            if (getConfig().getBoolean("delete-after-compress", true) || !Files.exists(finalZip)) {
                deleteFolder(staging);
            }
        }
    }

    /**
     * Keeps every backup from the rolling retention window and the earliest
     * successful backup from every calendar day indefinitely.
     */
    private void cleanupOldBackups() {
        int retentionHours = Math.max(1, getConfig().getInt("hourly-retention-hours", 12));
        LocalDateTime cutoff = LocalDateTime.now(ZoneId.of("Asia/Tokyo")).minusHours(retentionHours);
        Map<LocalDate, BackupFile> dailyBackups = new HashMap<>();
        List<BackupFile> backups = new ArrayList<>();

        try (var entries = Files.newDirectoryStream(backupDir.toPath(), "*.zip")) {
            for (Path entry : entries) {
                String fileName = entry.getFileName().toString();
                String timestamp = fileName.substring(0, fileName.length() - ".zip".length());
                try {
                    BackupFile backup = new BackupFile(entry, parseBackupTimestamp(timestamp));
                    backups.add(backup);
                    dailyBackups.merge(backup.createdAt().toLocalDate(), backup,
                            (first, candidate) -> Comparator.comparing(BackupFile::createdAt)
                                    .compare(first, candidate) <= 0 ? first : candidate);
                } catch (DateTimeParseException ignored) {
                    getLogger().warning("Skipping backup with an unknown filename: " + fileName);
                }
            }
        } catch (IOException e) {
            getLogger().warning("Could not scan old backups: " + e.getMessage());
            return;
        }

        for (BackupFile backup : backups) {
            boolean isDailyBackup = dailyBackups.get(backup.createdAt().toLocalDate()).equals(backup);
            if (!isDailyBackup && backup.createdAt().isBefore(cutoff)) {
                try {
                    Files.deleteIfExists(backup.path());
                    getLogger().info("Deleted expired hourly backup: " + backup.path().getFileName());
                } catch (IOException e) {
                    getLogger().warning("Could not delete old backup " + backup.path().getFileName() + ": " + e.getMessage());
                }
            }
        }
    }

    private LocalDateTime parseBackupTimestamp(String timestamp) {
        try {
            return LocalDateTime.parse(timestamp, BACKUP_NAME);
        } catch (DateTimeParseException ignored) {
            return LocalDateTime.parse(timestamp, LEGACY_BACKUP_NAME);
        }
    }

    private void copyFolder(Path source, Path target) throws IOException {
        Files.createDirectories(target);
        try (var entries = Files.newDirectoryStream(source)) {
            for (Path entry : entries) {
                Path destination = target.resolve(entry.getFileName());
                if (Files.isDirectory(entry)) copyFolder(entry, destination);
                else Files.copy(entry, destination, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            }
        }
    }

    private void compressToZip(Path sourceFolder, Path zipFile) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(zipFile)))) {
            zipFolder(sourceFolder, sourceFolder.getFileName().toString(), zos);
        }
    }

    private void zipFolder(Path folder, String parentPath, ZipOutputStream zos) throws IOException {
        byte[] buffer = new byte[8192];
        try (var entries = Files.newDirectoryStream(folder)) {
            for (Path entry : entries) {
                String entryPath = parentPath + "/" + entry.getFileName();
                if (Files.isDirectory(entry)) {
                    zipFolder(entry, entryPath, zos);
                } else {
                    zos.putNextEntry(new ZipEntry(entryPath));
                    try (BufferedInputStream input = new BufferedInputStream(Files.newInputStream(entry))) {
                        int length;
                        while ((length = input.read(buffer)) != -1) zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                }
            }
        }
    }

    private void deleteFolder(Path folder) {
        if (!Files.exists(folder)) return;
        try (var entries = Files.newDirectoryStream(folder)) {
            for (Path entry : entries) {
                if (Files.isDirectory(entry)) deleteFolder(entry);
                else Files.deleteIfExists(entry);
            }
            Files.deleteIfExists(folder);
        } catch (IOException e) {
            getLogger().warning("Could not delete temporary backup data: " + e.getMessage());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("simplebackup.admin")) {
            sender.sendMessage("§c権限がありません。");
            return true;
        }
        requestBackup(sender);
        return true;
    }

    private record WorldSaveState(World world, boolean autoSave) {}

    private record BackupFile(Path path, LocalDateTime createdAt) {}
}
