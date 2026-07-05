package com.suzunayui.memo;

import org.bukkit.plugin.java.JavaPlugin;

public class Memo extends JavaPlugin {

    private static Memo instance;
    private MemoManager memoManager;

    @Override
    public void onEnable() {
        instance = this;

        memoManager = new MemoManager(this);

        if (getCommand("memo") != null) {
            getCommand("memo").setExecutor(new MemoCommand(this));
        }

        if (getCommand("ms") != null) {
            getCommand("ms").setExecutor(new MemoSearchCommand(this));
        }

        if (getCommand("mn") != null) {
            getCommand("mn").setExecutor(new MemoNearCommand(this));
        }

        if (getCommand("md") != null) {
            getCommand("md").setExecutor(new MemoDeleteCommand(this));
        }

        if (getCommand("mp") != null) {
            getCommand("mp").setExecutor(new MemoPlayerCommand(this));
        }

        getLogger().info("Memo has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Memo has been disabled!");
    }

    public MemoManager getMemoManager() {
        return memoManager;
    }

    public static Memo getInstance() {
        return instance;
    }
}
