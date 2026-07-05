package com.suzunayui.memo;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MemoCommand implements CommandExecutor {

    private final Memo plugin;

    public MemoCommand(Memo plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("このコマンドはプレイヤーのみ使用できます。").color(NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Component.text("使用法: /memo <文字列>").color(NamedTextColor.RED));
            return true;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(args[i]);
        }
        String text = sb.toString();

        Location location = player.getLocation();
        String playerName = player.getName();

        int id = plugin.getMemoManager().addMemo(text, location, playerName);

        player.sendMessage(Component.text("メモを保存しました [#" + id + "]: " + text + " (" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")").color(NamedTextColor.GREEN));

        return true;
    }
}
