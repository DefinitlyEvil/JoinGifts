package org.dragonet.joingifts;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class JoinGifts extends JavaPlugin {

    private YamlConfiguration config;

    @Override
    public YamlConfiguration getConfig() {
        return config;
    }

    @Override
    public void onEnable() {
        getLogger().info("Loading configurations... ");
        reloadConfig();
        getLogger().info("Registering commands... ");
        getCommand("gifts").setExecutor(new GiftCommand(this));
        getCommand("gifts-reload").setExecutor(new GiftReloadCommand(this));
    }

    public String[] getGiftNames() {
        return config.getConfigurationSection("gifts").getKeys(false).toArray(new String[0]);
    }

    @Override
    public void reloadConfig() {
        saveResource("config.yml", false);
        config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
    }
}
