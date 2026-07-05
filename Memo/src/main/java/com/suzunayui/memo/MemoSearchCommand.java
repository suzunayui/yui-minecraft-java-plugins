package com.suzunayui.memo;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class MemoSearchCommand implements CommandExecutor {

    private final Memo plugin;

    public MemoSearchCommand(Memo plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text("使用法: /ms <検索文字列>").color(NamedTextColor.RED));
            return true;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(args[i]);
        }
        String keyword = sb.toString();

        List<String> results = plugin.getMemoManager().searchMemos(keyword);

        if (results.isEmpty()) {
            sender.sendMessage(Component.text("該当するメモが見つかりませんでした。").color(NamedTextColor.RED));
            return true;
        }

        sender.sendMessage(Component.text("検索結果: " + results.size() + "件").color(NamedTextColor.YELLOW));
        for (String result : results) {
            sender.sendMessage(Component.text(result).color(NamedTextColor.AQUA));
        }

        return true;
    }
}
