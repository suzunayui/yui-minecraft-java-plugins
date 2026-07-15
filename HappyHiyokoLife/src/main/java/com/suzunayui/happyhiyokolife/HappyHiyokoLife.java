package com.suzunayui.happyhiyokolife;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HappyHiyokoLife extends JavaPlugin implements Listener {

    private final Map<UUID, BossBar> bossBars = new HashMap<>();
    private final Map<UUID, Integer> hitCounts = new HashMap<>();
    private final Map<UUID, Boolean> raidActive = new HashMap<>();
    private final Map<UUID, Integer> raidKillCounts = new HashMap<>();
    private final Map<UUID, Integer> raidCurrentTurn = new HashMap<>();
    private final Map<UUID, BukkitTask> raidTasks = new HashMap<>();
    private final Map<UUID, UUID> raidChickenOwners = new HashMap<>();

    private static final String KING_NAME = "§6キングすずなゆい";
    private static final String RETRY_NAME = "§eまたやる？";
    private static final int MAX_HITS = 10;
    private static final int CHICKENS_PER_TURN = 10;
    private static final int TOTAL_TURNS = 2;
    private static final double SCALE_VALUE = 3.0;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("HappyHiyokoLife has been enabled!");
    }

    @Override
    public void onDisable() {
        for (BossBar bar : bossBars.values()) {
            bar.removeAll();
        }
        bossBars.clear();
        hitCounts.clear();
        raidActive.clear();
        raidKillCounts.clear();
        raidCurrentTurn.clear();
        for (BukkitTask task : raidTasks.values()) {
            task.cancel();
        }
        raidTasks.clear();
        raidChickenOwners.clear();
        getLogger().info("HappyHiyokoLife has been disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("このコマンドはプレイヤーのみ実行できます。");
            return true;
        }

        if (!command.getName().equalsIgnoreCase("hiyoko")) {
            return false;
        }

        Location spawnLoc = player.getLocation();
        Chicken chicken = (Chicken) player.getWorld().spawnEntity(spawnLoc, EntityType.CHICKEN);
        chicken.setCustomName(KING_NAME);
        chicken.setCustomNameVisible(true);
        chicken.setAI(true);
        chicken.setRemoveWhenFarAway(false);

        try {
            var scaleAttribute = Attribute.valueOf("GENERIC_SCALE");
            chicken.getAttribute(scaleAttribute).setBaseValue(SCALE_VALUE);
        } catch (IllegalArgumentException ignored) {
        }

        UUID chickenId = chicken.getUniqueId();
        hitCounts.put(chickenId, 0);
        raidActive.put(chickenId, false);

        BossBar bossBar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);
        bossBar.setProgress(0.0);
        bossBar.addPlayer(player);
        bossBars.put(chickenId, bossBar);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage("§e大きいひよこだよ！かわいいね！いじめないでね！！！");
        }

        return true;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Chicken chicken)) {
            return;
        }

        UUID chickenId = chicken.getUniqueId();
        if (!hitCounts.containsKey(chickenId)) {
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.CUSTOM) {
            return;
        }

        LivingEntity livingChicken = chicken;
        livingChicken.playHurtAnimation(0.0f);

        event.setCancelled(true);

        if (raidActive.getOrDefault(chickenId, false)) {
            return;
        }

        if (!(event instanceof EntityDamageByEntityEvent damageByEntityEvent)) {
            return;
        }

        if (!(damageByEntityEvent.getDamager() instanceof Player)) {
            return;
        }

        int hits = hitCounts.getOrDefault(chickenId, 0) + 1;
        hitCounts.put(chickenId, hits);

        BossBar bossBar = bossBars.get(chickenId);
        if (bossBar != null) {
            double progress = (double) hits / MAX_HITS;
            bossBar.setProgress(Math.min(progress, 1.0));
            bossBar.setTitle("§6キングすずなゆい §c[" + (hits * 10) + "%]");

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!bossBar.getPlayers().contains(onlinePlayer)) {
                    bossBar.addPlayer(onlinePlayer);
                }
            }
        }

        if (hits >= MAX_HITS) {
            startRaid(chicken);
        }
    }

    private void startRaid(Chicken kingChicken) {
        UUID chickenId = kingChicken.getUniqueId();
        raidActive.put(chickenId, true);
        raidKillCounts.put(chickenId, 0);
        raidCurrentTurn.put(chickenId, 1);

        BossBar bossBar = bossBars.get(chickenId);
        if (bossBar != null) {
            bossBar.setTitle("§c襲撃開始！");
            bossBar.setColor(BarColor.RED);
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage("§c§l袭撃が始まる！にわとりたちが襲いかかってくる！");
        }

        spawnRaidChickens(kingChicken, 1);
    }

    private void spawnRaidChickens(Chicken kingChicken, int turn) {
        UUID chickenId = kingChicken.getUniqueId();
        Location center = kingChicken.getLocation();

        for (int i = 0; i < CHICKENS_PER_TURN; i++) {
            double angle = (2 * Math.PI / CHICKENS_PER_TURN) * i;
            double offsetX = 10 * Math.cos(angle);
            double offsetZ = 10 * Math.sin(angle);
            Location spawnLoc = center.clone().add(offsetX, 0, offsetZ);

            Bukkit.getScheduler().runTaskLater(this, () -> {
                if (!raidActive.getOrDefault(chickenId, false)) {
                    return;
                }

                Chicken raidChicken = (Chicken) center.getWorld().spawnEntity(spawnLoc, EntityType.CHICKEN);
                raidChicken.setCustomName("§cにわとり");
                raidChicken.setCustomNameVisible(false);
                raidChicken.setTarget(getNearestPlayer(center));
                raidChickenOwners.put(raidChicken.getUniqueId(), chickenId);
            }, i * 5L);
        }

        BukkitTask task = Bukkit.getScheduler().runTaskLater(this, () -> {
            if (!raidActive.getOrDefault(chickenId, false)) {
                return;
            }

            int currentTurn = raidCurrentTurn.getOrDefault(chickenId, 1);
            if (currentTurn < TOTAL_TURNS) {
                raidCurrentTurn.put(chickenId, currentTurn + 1);
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.sendMessage("§c§l第" + (currentTurn + 1) + "ターン開始！");
                }
                spawnRaidChickens(kingChicken, currentTurn + 1);
            }
        }, 600L);

        raidTasks.put(chickenId, task);
    }

    private Player getNearestPlayer(Location location) {
        Player nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Player player : Bukkit.getOnlinePlayers()) {
            double distance = player.getLocation().distance(location);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = player;
            }
        }

        return nearest;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Chicken chicken)) {
            return;
        }

        UUID chickenId = chicken.getUniqueId();

        if (raidChickenOwners.containsKey(chickenId)) {
            UUID kingChickenId = raidChickenOwners.get(chickenId);
            raidChickenOwners.remove(chickenId);

            if (!raidActive.getOrDefault(kingChickenId, false)) {
                return;
            }

            int kills = raidKillCounts.getOrDefault(kingChickenId, 0) + 1;
            raidKillCounts.put(kingChickenId, kills);

            int totalRequired = CHICKENS_PER_TURN * TOTAL_TURNS;
            BossBar bossBar = bossBars.get(kingChickenId);
            if (bossBar != null) {
                double progress = (double) kills / totalRequired;
                bossBar.setProgress(Math.min(progress, 1.0));
                bossBar.setTitle("§c襲撃中！ [" + kills + "/" + totalRequired + "]");
            }

            if (kills >= totalRequired) {
                endRaid(kingChickenId);
            }

            return;
        }

        if (!hitCounts.containsKey(chickenId)) {
            return;
        }

        BossBar bossBar = bossBars.remove(chickenId);
        if (bossBar != null) {
            bossBar.removeAll();
        }
        hitCounts.remove(chickenId);
        raidActive.remove(chickenId);
        raidKillCounts.remove(chickenId);
        raidCurrentTurn.remove(chickenId);

        BukkitTask task = raidTasks.remove(chickenId);
        if (task != null) {
            task.cancel();
        }
    }

    private void endRaid(UUID kingChickenId) {
        raidActive.put(kingChickenId, false);

        for (Map.Entry<UUID, UUID> entry : raidChickenOwners.entrySet()) {
            if (entry.getValue().equals(kingChickenId)) {
                Entity entity = Bukkit.getEntity(entry.getKey());
                if (entity != null) {
                    entity.remove();
                }
            }
        }

        raidChickenOwners.entrySet().removeIf(entry -> entry.getValue().equals(kingChickenId));

        BukkitTask task = raidTasks.remove(kingChickenId);
        if (task != null) {
            task.cancel();
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage("§a§l襲撃を打ち勝った！");
        }

        BossBar bossBar = bossBars.get(kingChickenId);
        if (bossBar != null) {
            bossBar.removeAll();
            bossBars.remove(kingChickenId);
        }

        hitCounts.remove(kingChickenId);
        raidKillCounts.remove(kingChickenId);
        raidCurrentTurn.remove(kingChickenId);

        Entity entity = Bukkit.getEntity(kingChickenId);
        if (entity instanceof Chicken kingChicken) {
            kingChicken.setCustomName(RETRY_NAME);

            Bukkit.getScheduler().runTaskLater(this, () -> {
                if (kingChicken.isValid()) {
                    kingChicken.setCustomName(KING_NAME);
                }
            }, 1200L);
        }
    }
}
