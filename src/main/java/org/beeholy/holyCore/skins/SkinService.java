package org.beeholy.holyCore.skins;

import org.beeholy.holyCore.HolyCore;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;

public class SkinService {
    private final JavaPlugin plugin;
    private SkinManager skinManager;

    public SkinService(JavaPlugin plugin){
        this.plugin = plugin;
        reload();
    }

    public SkinManager getSkinManager() {
        return skinManager;
    }

    public void reload(){
        SkinRegistry registry = new SkinRegistry();

        File file = new File(plugin.getDataFolder(), "skins.yml");

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource("skins.yml", false); // copies from jar if present
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        registry.registerAll(
                SkinConfigLoader.load(config)
        );

        this.skinManager = new SkinManager(registry);
    }
}
