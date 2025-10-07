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

public class Tags {

    private static HashMap<String, String> tags;
    private static final Tags instance = new Tags();

    private static File tagsFile;
    private static FileConfiguration tagsConfig;

    static {

    }

    public static void setup(){
        JavaPlugin plugin = HolyCore.getInstance();
        tags = new HashMap<>();
        tagsFile = new File(plugin.getDataFolder(), "tags.yml");

        if (!tagsFile.exists()) {
            tagsFile.getParentFile().mkdirs();
            plugin.saveResource("tags.yml", false); // copies from jar if present
        }

        tagsConfig = YamlConfiguration.loadConfiguration(tagsFile);
        // From config, load into static hashmap
        for (String key : tagsConfig.getKeys(false)) {
            String value = tagsConfig.getString(key);
            if (value != null) {
                tags.put(key, value);
            }
        }
    }
    public static void reload() {
        tags.clear(); // Clear old tags
        tagsConfig = YamlConfiguration.loadConfiguration(tagsFile);

        for (String key : tagsConfig.getKeys(false)) {
            String value = tagsConfig.getString(key);
            if (value != null) {
                tags.put(key, value);
            }
        }
    }

    public static String getTag(String name){
        return tags.getOrDefault(name, "");
    }

    // getTagNames
    public static List<String> getTagNames(){
        return new java.util.ArrayList<>(tags.keySet());
    }

    public static List<String> getTags(){
        return new java.util.ArrayList<>(tags.keySet());
    }

    public static List<String> getPlayerTags(Player user){
        // check player permissions for tag.<name> and make sure its in config, return list of tag names
        List<String> playerTags = new java.util.ArrayList<>();

        for (String key : tags.keySet()) {
            if (user.hasPermission("tag." + key)) {
                playerTags.add(key);
            }
        }

        return playerTags;
    }
    public static boolean setPlayerTag(Player player, String tagName) {
        if (tags.containsKey(tagName) || tagName.isEmpty()) {
            PlayerData.setString(player, "tag_name", tagName);
            return true;
        } else {
            return false;
        }
    }

    public static String getPlayerTag(Player player) {
        NamespacedKey key = new NamespacedKey(HolyCore.getInstance(), "tag_name");
        String tagName =  player.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        String tag = getTag(tagName);
        return tagName != null ? tag : "";
    }
    public static Tags getInstance(){
        return instance;
    }
}
