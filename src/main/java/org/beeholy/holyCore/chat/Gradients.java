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

public class Gradients {

    private static final Gradients instance = new Gradients();
    private static HashMap<String, String> gradients;
    private static File gradientsFile;
    private static FileConfiguration gradientsConfig;

    static {

    }

    public static void setup() {
        JavaPlugin plugin = HolyCore.getInstance();
        gradients = new HashMap<>();
        gradientsFile = new File(plugin.getDataFolder(), "gradients.yml");

        if (!gradientsFile.exists()) {
            gradientsFile.getParentFile().mkdirs();
            plugin.saveResource("gradients.yml", false); // copies from jar if present
        }

        gradientsConfig = YamlConfiguration.loadConfiguration(gradientsFile);
        // From config, load into static hashmap
        for (String key : gradientsConfig.getKeys(false)) {
            String value = gradientsConfig.getString(key);
            if (value != null) {
                gradients.put(key, value);
            }
        }
    }

    public static void reload() {
        gradients.clear(); // Clear old tags
        gradientsConfig = YamlConfiguration.loadConfiguration(gradientsFile);

        for (String key : gradientsConfig.getKeys(false)) {
            String value = gradientsConfig.getString(key);
            if (value != null) {
                gradients.put(key, value);
            }
        }
    }

    public static String getGradient(String name) {
        return gradients.getOrDefault(name, "<white>");
    }

    public static List<String> getGradients() {
        return new java.util.ArrayList<>(gradients.keySet());
    }

    public static List<String> getPlayerGradients(Player user) {
        // check player permissions for tag.<name> and make sure its in config, return list of tag names
        List<String> playerTags = new java.util.ArrayList<>();

        for (String key : gradients.keySet()) {
            if (user.hasPermission("gradient." + key)) {
                playerTags.add(key);
            }
        }

        return playerTags;
    }

    public static boolean setPlayerGradient(Player player, String gradientName) {
        if (gradients.containsKey(gradientName) || gradientName.isEmpty()) {
            PlayerData.setString(player, "gradient_name", gradientName);
            return true;
        } else {
            return false;
        }
    }

    public static String getPlayerGradient(Player player) {
        NamespacedKey key = new NamespacedKey(HolyCore.getInstance(), "gradient_name");
        String gradientName = player.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        String gradient = getGradient(gradientName);
        return gradientName != null ? gradient : "<white>";
    }

    public static Gradients getInstance() {
        return instance;
    }
}