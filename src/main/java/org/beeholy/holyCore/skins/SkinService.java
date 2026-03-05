package org.beeholy.holyCore.skins;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

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
