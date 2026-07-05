package com.suzunayui.memo;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MemoDeleteCommand implements CommandExecutor {

    private final Memo plugin;

    public MemoDeleteCommand(Memo plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text("使用法: /md <メモID>").color(NamedTextColor.RED));
            return true;
        }

        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("IDは数字で指定してください。").color(NamedTextColor.RED));
            return true;
        }

        String owner = plugin.getMemoManager().getMemoOwner(id);
        if (owner == null) {
            sender.sendMessage(Component.text("メモ #" + id + " が見つかりませんでした。").color(NamedTextColor.RED));
            return true;
        }

        if (sender instanceof Player player) {
            if (!player.isOp() && !player.getName().equalsIgnoreCase(owner)) {
                player.sendMessage(Component.text("自分のメモだけを削除できます。").color(NamedTextColor.RED));
                return true;
            }
        }

        plugin.getMemoManager().deleteMemo(id);
        sender.sendMessage(Component.text("メモ #" + id + " を削除しました。").color(NamedTextColor.GREEN));

        return true;
    }
}
