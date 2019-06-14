package org.dragonet.joingifts;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GiftReloadCommand implements CommandExecutor {

    private final JoinGifts plugin;

    public GiftReloadCommand(JoinGifts plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("gifts.reload")) {
            sender.sendMessage("\u00a7cNo permission! ");
            return true;
        }
        plugin.reloadConfig();
        sender.sendMessage("\u00a7aGifts reloaded! ");
        return true;
    }
}
