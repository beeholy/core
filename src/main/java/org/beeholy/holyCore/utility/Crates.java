package org.beeholy.holyCore.utility;

import org.beeholy.holyCore.HolyCore;
import org.beeholy.holyCore.model.Crate;
import org.beeholy.holyCore.model.Reward;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Crates {
    private static File cratesFile;
    private static FileConfiguration cratesConfig;
    private static Map<String, Crate> crates;
    private static Set<String> bannedWorlds = new HashSet<>();

    public static void setup() {
        cratesFile = new File(HolyCore.getInstance().getDataFolder(), "crates.yml");
        crates = new HashMap<>();
        if (!cratesFile.exists()) {
            cratesFile.getParentFile().mkdirs();
            HolyCore.getInstance().saveResource("crates.yml", false);
        }

        cratesConfig = YamlConfiguration.loadConfiguration(cratesFile);
        crates.putAll(loadCrates(cratesConfig.getConfigurationSection("crates")));
        if(!(cratesConfig.getStringList("banned_worlds").isEmpty())){
            bannedWorlds.addAll(cratesConfig.getStringList("banned_worlds"));
        }
    }

    public static void reload() {
        crates.clear();
        cratesConfig = YamlConfiguration.loadConfiguration(cratesFile);
        crates.putAll(loadCrates(cratesConfig.getConfigurationSection("crates")));
        if(!(cratesConfig.getStringList("banned_worlds").isEmpty())){
            bannedWorlds.addAll(cratesConfig.getStringList("banned_worlds"));
        }
    }

    public static boolean isWorldBanned(World world){
        return bannedWorlds.contains(world.getName());
    }

    public static void save() {
        try {
            cratesConfig.save(cratesFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Crate> loadCrates(ConfigurationSection config) {
        Map<String, Crate> crates = new HashMap<>();

        for (String crateKey : config.getKeys(false)) {
            ConfigurationSection crateSection = config.getConfigurationSection(crateKey);
            String name = crateSection.getString("name");
            Location location = crateSection.getLocation("location");
            List<Reward> rewards = new ArrayList<>();
            ConfigurationSection rewardsSection = crateSection.getConfigurationSection("rewards");

            for (String rewardKey : rewardsSection.getKeys(false)) {
                ConfigurationSection rewardData = rewardsSection.getConfigurationSection(rewardKey);
                String rewardName = rewardData.getString("name");
                String rewardMaterial = rewardData.getString("material");
                String customModel = rewardData.getString("custom_model");
                List<String> lore = rewardData.getStringList("lore");
                double chance = rewardData.getDouble("chance");
                String command = rewardData.getString("command");
                int stackSize = rewardData.getInt("stack_size", 1);
                rewards.add(new Reward(rewardName, lore, chance, command, rewardMaterial, customModel, stackSize));
            }
            String keyName = crateSection.getString("key.name");
            List<String> keyLore = (List<String>) crateSection.getList("key.lore");
            String keyCustomModel = crateSection.getString("key.custom_model");
            crates.put(crateKey, new Crate(crateKey, name, rewards, location, keyName, keyLore, keyCustomModel));
        }

        return crates;
    }

    public static Crate fromLocation(Location location) {
        for (Crate crate : crates.values()) {
            if (crate.getLocation().equals(location)) {
                return crate;
            }
        }
        return null;
    }

    public static Reward rollReward(List<Reward> rewards) {
        double totalWeight = rewards.stream().mapToDouble(Reward::getChance).sum();
        double random = Math.random() * totalWeight;

        for (Reward reward : rewards) {
            random -= reward.getChance();
            if (random <= 0) return reward;
        }

        return rewards.getFirst(); // fallback
    }

    public static Map<String, Crate> getCrates() {
        return crates;
    }

    public static FileConfiguration getCratesConfig() {
        return cratesConfig;
    }
}
