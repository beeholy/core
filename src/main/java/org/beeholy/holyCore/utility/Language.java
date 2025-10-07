package org.beeholy.holyCore.utility;

import org.beeholy.holyCore.HolyCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

public class Language {
    private static final HolyCore plugin = HolyCore.getInstance();
    private static HashMap<String, String> strings;
    private static File file;
    private static FileConfiguration config;

    public static void setup() {
        file = new File(HolyCore.getInstance().getDataFolder(), "language.yml");
        strings = new HashMap<>();

        reload();
    }

    public static void reload() {
        strings.clear();
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource("language.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(file);

        for (String key : config.getKeys(false)) {
            String value = config.getString(key);
            if (value != null) {
                strings.put(key, value);
            }
        }
    }

    public static String get(String key) {
        if (strings.containsKey(key))
            return strings.get(key);
        return "";
    }

}
