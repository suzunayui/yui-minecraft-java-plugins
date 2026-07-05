package com.suzunayui.memo;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MemoManager {

    private final Memo plugin;
    private final File dataFile;
    private YamlConfiguration data;

    public MemoManager(Memo plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "memos.yml");
        loadData();
    }

    private void loadData() {
        if (!dataFile.exists()) {
            plugin.getDataFolder().mkdirs();
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
    }

    private void saveData() {
        try {
            data.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("メモの保存に失敗しました: " + e.getMessage());
        }
    }

    public int addMemo(String text, Location location, String playerName) {
        List<?> memos = data.getList("memos");
        if (memos == null) {
            memos = new ArrayList<>();
        }
        List<Object> memoList = new ArrayList<>(memos);

        int nextId = data.getInt("nextId", 1);

        java.util.Map<String, Object> memoEntry = new java.util.LinkedHashMap<>();
        memoEntry.put("id", nextId);
        memoEntry.put("text", text);
        memoEntry.put("world", location.getWorld().getName());
        memoEntry.put("x", location.getBlockX());
        memoEntry.put("y", location.getBlockY());
        memoEntry.put("z", location.getBlockZ());
        memoEntry.put("player", playerName);

        memoList.add(memoEntry);
        data.set("memos", memoList);
        data.set("nextId", nextId + 1);
        saveData();
        return nextId;
    }

    public List<String> searchMemos(String keyword) {
        List<String> results = new ArrayList<>();
        List<?> memos = data.getList("memos");
        if (memos == null) {
            return results;
        }

        String lowerKeyword = keyword.toLowerCase();

        for (Object obj : memos) {
            if (obj instanceof java.util.Map<?, ?> map) {
                String text = String.valueOf(map.get("text"));
                if (text.toLowerCase().contains(lowerKeyword)) {
                    int id = (int) map.get("id");
                    String world = String.valueOf(map.get("world"));
                    int x = (int) map.get("x");
                    int y = (int) map.get("y");
                    int z = (int) map.get("z");
                    String player = String.valueOf(map.get("player"));

                    results.add("#" + id + " " + text + " @ " + world + " (" + x + ", " + y + ", " + z + ") by " + player);
                }
            }
        }

        return results;
    }

    public List<String> findNearMemos(Location playerLocation, int limit) {
        List<String[]> memoEntries = new ArrayList<>();
        List<?> memos = data.getList("memos");
        if (memos == null) {
            return new ArrayList<>();
        }

        String worldName = playerLocation.getWorld().getName();

        for (Object obj : memos) {
            if (obj instanceof java.util.Map<?, ?> map) {
                String world = String.valueOf(map.get("world"));
                if (!world.equals(worldName)) continue;

                int id = (int) map.get("id");
                int x = (int) map.get("x");
                int y = (int) map.get("y");
                int z = (int) map.get("z");
                String text = String.valueOf(map.get("text"));
                String player = String.valueOf(map.get("player"));

                double distance = Math.sqrt(
                        Math.pow(playerLocation.getBlockX() - x, 2)
                        + Math.pow(playerLocation.getBlockY() - y, 2)
                        + Math.pow(playerLocation.getBlockZ() - z, 2)
                );

                memoEntries.add(new String[]{String.valueOf(id), text, world, String.valueOf(x), String.valueOf(y), String.valueOf(z), player, String.valueOf((int) distance)});
            }
        }

        memoEntries.sort(Comparator.comparingInt(a -> Integer.parseInt(a[7])));

        List<String> results = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, memoEntries.size()); i++) {
            String[] e = memoEntries.get(i);
            results.add("#" + e[0] + " " + e[1] + " @ " + e[2] + " (" + e[3] + ", " + e[4] + ", " + e[5] + ") [" + e[7] + "m] by " + e[6]);
        }
        return results;
    }

    public List<String> findMemosByPlayer(String playerName, int limit) {
        List<String> results = new ArrayList<>();
        List<?> memos = data.getList("memos");
        if (memos == null) {
            return results;
        }

        List<Object> memoList = new ArrayList<>(memos);
        for (int i = memoList.size() - 1; i >= 0; i--) {
            Object obj = memoList.get(i);
            if (obj instanceof java.util.Map<?, ?> map) {
                String player = String.valueOf(map.get("player"));
                if (player.equalsIgnoreCase(playerName)) {
                    int id = (int) map.get("id");
                    String text = String.valueOf(map.get("text"));
                    String world = String.valueOf(map.get("world"));
                    int x = (int) map.get("x");
                    int y = (int) map.get("y");
                    int z = (int) map.get("z");
                    results.add("#" + id + " " + text + " @ " + world + " (" + x + ", " + y + ", " + z + ") by " + player);
                    if (results.size() >= limit) break;
                }
            }
        }
        return results;
    }

    public String getMemoOwner(int id) {
        List<?> memos = data.getList("memos");
        if (memos == null) {
            return null;
        }
        for (Object obj : memos) {
            if (obj instanceof java.util.Map<?, ?> map) {
                if ((int) map.get("id") == id) {
                    return String.valueOf(map.get("player"));
                }
            }
        }
        return null;
    }

    public boolean deleteMemo(int id) {
        List<?> memos = data.getList("memos");
        if (memos == null) {
            return false;
        }
        List<Object> memoList = new ArrayList<>(memos);

        for (int i = 0; i < memoList.size(); i++) {
            Object obj = memoList.get(i);
            if (obj instanceof java.util.Map<?, ?> map) {
                if ((int) map.get("id") == id) {
                    memoList.remove(i);
                    data.set("memos", memoList);
                    saveData();
                    return true;
                }
            }
        }
        return false;
    }
}
