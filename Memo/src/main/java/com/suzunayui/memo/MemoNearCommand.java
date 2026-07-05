package com.suzunayui.memo;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MemoNearCommand implements CommandExecutor {

    private final Memo plugin;

    public MemoNearCommand(Memo plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("このコマンドはプレイヤーのみ使用できます。").color(NamedTextColor.RED));
            return true;
        }

        Location location = player.getLocation();
        List<String> results = plugin.getMemoManager().findNearMemos(location, 5);

        if (results.isEmpty()) {
            player.sendMessage(Component.text("近くのメモが見つかりませんでした。").color(NamedTextColor.RED));
            return true;
        }

        player.sendMessage(Component.text("近くのメモ (" + results.size() + "件):").color(NamedTextColor.YELLOW));
        for (String result : results) {
            player.sendMessage(Component.text(result).color(NamedTextColor.AQUA));
        }

        return true;
    }
}
