package org.dragonet.joingifts;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.dragonet.bukkit.menuapi.ItemMenu;
import org.dragonet.bukkit.menuapi.ItemMenuInstance;
import org.dragonet.bukkit.menuapi.MenuAPIPlugin;
import org.dragonet.profileapi.PlayerProfile;
import org.dragonet.profileapi.ProfileAPI;

import java.util.ArrayList;
import java.util.List;

public class GiftCommand implements CommandExecutor {

    private final JoinGifts plugin;

    public GiftCommand(JoinGifts plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!Player.class.isAssignableFrom(sender.getClass())) return true;
        if(!sender.hasPermission("gifts.use")) {
            sender.sendMessage("\u00a7cNo permission! ");
            return true;
        }
        Player player = (Player) sender;
        if(args.length > 0) {
            tryGetGift(player, args[0]);
            return true;
        }
        String[] gift_keys = plugin.getGiftNames();
        ConfigurationSection gift_entries = plugin.getConfig().getConfigurationSection("gifts");
        ItemMenuInstance menu = new ItemMenuInstance(plugin.getConfig().getString("title", "\u00a7eGifts to Claim"), gift_keys.length + 9);

        PlayerProfile profile = ProfileAPI.getInstance().getProfileFor(player);
        List<String> acquired_gifts = profile.getData().contains("join_gifts") ? profile.getData().getStringList("join_gifts") : null;

        for(int i = 0; i < gift_keys.length; i++) {
            try {
                ConfigurationSection data = gift_entries.getConfigurationSection(gift_keys[i]);
                String name = data.getString("name", "NO NAME");
                Material icon = Material.valueOf(data.getString("icon", "BARRIER"));
                List<String> lore = data.contains("lore") ? data.getStringList("lore") : null;
                if(acquired_gifts != null && acquired_gifts.contains(gift_keys[i])) {
                    name += " \u00a77(\u00a7c\u00a7aACQUIRED\u00a77)";
                } else {
                    if (data.contains("permission") && data.getBoolean("permission")) {
                        if (!player.hasPermission("gifts.get." + gift_keys[i])) {
                            name += " \u00a77(\u00a7c\u00a7lNO PERM\u00a77)";
                        }
                    }
                }
                name = name.replace("&", "\u00a7");
                final String k = gift_keys[i];
                if(lore != null) {
                    List<String> lore_temp = lore;
                    lore = new ArrayList<>();
                    for(String l : lore_temp) {
                        lore.add(l.replace("&", "\u00a7"));
                    }
                }
                menu.setButton(i, icon, name, lore, (humanEntity, itemMenuInstance) -> {
                    tryGetGift(player, k);
                    player.closeInventory();
                });
                player.sendMessage("\u00a7aOpened gifts menu! ");
            } catch (Exception e) {
                e.printStackTrace();
                player.sendMessage("\u00a7cOops, server error happened! ");
            }
        }

        MenuAPIPlugin.getMenuAPI().open(player, menu);
        return true;
    }

    private void tryGetGift(Player p, String key) {
        if(!plugin.getConfig().contains("gifts." + key)) {
            p.sendMessage("\u00a7cGift does not exist! ");
            return;
        }
        PlayerProfile profile = ProfileAPI.getInstance().getProfileFor(p);
        if(profile.getData().contains("join_gifts") && profile.getData().getStringList("join_gifts").contains(key)) {
            p.sendMessage("\u00a7cYou already have that gift! ");
            return;
        }
        ConfigurationSection data = plugin.getConfig().getConfigurationSection("gifts." + key);
        if(data.contains("permission") && data.getBoolean("permission") && !p.hasPermission("gifts.get." + key)) {
            p.sendMessage("\u00a7cNo permission to get this gift! ");
            return;
        }
        List<String> acquired = profile.getData().getStringList("join_gifts");
        acquired.add(key);
        profile.getData().set("join_gifts", acquired);
        profile.save();
        List<String> commands = data.getStringList("commands");
        for(String l : commands) {
            String l_replaced = l
                    .replace("{player}", p.getName())
                    .replace("{uuid}", p.getUniqueId().toString());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), l_replaced);
        }
        p.sendMessage("\u00a7aYou successfully claimed that gift! ");
    }
}
