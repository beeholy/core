package org.beeholy.holyCore.chat;

import org.beeholy.holyCore.HolyCore;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class Colors {

    private static final Colors instance = new Colors();
    private static HashMap<String, String> colors;
    private static File colorsFile;
    private static FileConfiguration colorsConfig;

    static {

    }

    public static void setup() {
        JavaPlugin plugin = HolyCore.getInstance();
        colors = new HashMap<>();
        colorsFile = new File(plugin.getDataFolder(), "colors.yml");

        if (!colorsFile.exists()) {
            colorsFile.getParentFile().mkdirs();
            plugin.saveResource("colors.yml", false); // copies from jar if present
        }

        colorsConfig = YamlConfiguration.loadConfiguration(colorsFile);
        // From config, load into static hashmap
        for (String key : colorsConfig.getKeys(false)) {
            String value = colorsConfig.getString(key);
            if (value != null) {
                colors.put(key, value);
            }
        }
    }

    public static void reload() {
        colors.clear(); // Clear old tags
        colorsConfig = YamlConfiguration.loadConfiguration(colorsFile);

        for (String key : colorsConfig.getKeys(false)) {
            String value = colorsConfig.getString(key);
            if (value != null) {
                colors.put(key, value);
            }
        }
    }

    public static String getColor(String name) {
        return colors.getOrDefault(name, "#FFFFFF");
    }

    public static List<String> getPlayerColors(Player user) {
        // check player permissions for tag.<name> and make sure its in config, return list of tag names
        List<String> playerColors = new java.util.ArrayList<>();

        for (String key : colors.keySet()) {
            if (user.hasPermission("color." + key)) {
                playerColors.add(key);
            }
        }

        return playerColors;
    }

    public static List<String> getColors() {
        List<String> allColors = new java.util.ArrayList<>();
        for (String key : colors.keySet()) {
            allColors.add(key);
        }
        return allColors;
    }

    public static boolean setPlayerColor(Player player, String colorName) {
        if (colors.containsKey(colorName) || colorName.isEmpty()) {
            PlayerData.setString(player, "color_name", colorName);
            return true;
        } else {
            return false;
        }
    }

    public static String getPlayerColor(Player player) {
        NamespacedKey key = new NamespacedKey(HolyCore.getInstance(), "color_name");
        String colorName = player.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        String color = getColor(colorName);
        return colorName != null ? color : "#FFFFFF";
    }

    public static Colors getInstance() {
        return instance;
    }
}