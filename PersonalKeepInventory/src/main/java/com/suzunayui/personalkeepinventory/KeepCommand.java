package com.suzunayui.personalkeepinventory;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KeepCommand implements CommandExecutor {

    private final PersonalKeepInventory plugin;

    public KeepCommand(PersonalKeepInventory plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("このコマンドはプレイヤーのみ使用できます。").color(NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            plugin.getKeepManager().toggleKeepInventory(player.getUniqueId());
        } else {
            String arg = args[0].toLowerCase();
            if (arg.equals("on")) {
                plugin.getKeepManager().setKeepInventory(player.getUniqueId(), true);
            } else if (arg.equals("off")) {
                plugin.getKeepManager().setKeepInventory(player.getUniqueId(), false);
            } else {
                player.sendMessage(Component.text("使い方: /keep [on|off]").color(NamedTextColor.RED));
                return true;
            }
        }

        boolean enabled = plugin.getKeepManager().isKeepInventory(player.getUniqueId());
        String status = enabled ? "ON" : "OFF";
        NamedTextColor color = enabled ? NamedTextColor.GREEN : NamedTextColor.RED;

        player.sendMessage(
            Component.text("死亡時のアイテム保持: ").color(NamedTextColor.GRAY)
                .append(Component.text(status).color(color))
        );

        return true;
    }
}
