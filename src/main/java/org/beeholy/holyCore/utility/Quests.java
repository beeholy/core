package org.beeholy.holyCore.utility;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.apache.commons.lang.ArrayUtils;
import org.beeholy.holyCore.HolyCore;
import org.beeholy.holyCore.gui.QuestsGUI;
import org.beeholy.holyCore.model.Quest;
import org.beeholy.holyCore.model.QuestItem;
import org.beeholy.holyCore.model.QuestReward;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Quests {
    private static HashMap<String, Quest> quests = new HashMap<>();

    private static List<String> completed_lore;
    private static String completed_tag;
    private static String claim_lore;
    private static String status_lore;

    public static ConfigurationSection getPagesSection() {
        return pagesSection;
    }

    private static ConfigurationSection pagesSection;

    public static String getClaim_lore() {
        return claim_lore;
    }

    public static String getStatus_lore() {
        return status_lore;
    }

    public static String getCompleted_tag() {
        return completed_tag;
    }

    public static List<String> getCompleted_lore() {
        return completed_lore;
    }



    public static HashMap<String, Quest> getQuests() {
        return quests;
    }

    public static void reload() {
        File questFile;
        FileConfiguration questConfig;
        HolyCore plugin = HolyCore.getInstance();

        questFile = new File(plugin.getDataFolder(), "quests.yml");

        if (!questFile.exists()) {
            questFile.getParentFile().mkdirs();
            plugin.saveResource("quests.yml", false); // copies from jar if present
        }

        questConfig = YamlConfiguration.loadConfiguration(questFile);

        // Reload Quests hashmap
        if (quests != null) quests.clear();

        completed_lore = questConfig.getStringList("global.completed_lore");
        completed_tag = questConfig.getString("global.completed_tag");
        claim_lore = questConfig.getString("global.claim_lore");
        status_lore = questConfig.getString("global.status_lore");

        pagesSection = questConfig.getConfigurationSection("pages");

        ConfigurationSection questsSection = questConfig.getConfigurationSection("quests");
        for (String key : questsSection.getKeys(false)) {
            String name = questsSection.getString(key + ".name");
            String statisticString = questsSection.getString(key + ".statistic");
            String materialString = questsSection.getString(key + ".material");
            String entityString = questsSection.getString(key + ".entity");

            ArrayList<String> loreStrings = new ArrayList<>();

            if(questsSection.isList(key + ".lore")){
                loreStrings.addAll(questsSection.getStringList(key + ".lore"));
            } else if (questsSection.isString(key + ".lore")){
                loreStrings.add(questsSection.getString(key + ".lore"));
            }

            // Quest Variable Validation
            if (name == null) {
                plugin.getLogger().warning("quest " + key + " has no name");
                return;
            }
            if (statisticString == null) {
                plugin.getLogger().warning("quest " + key + " needs a statistic type");
                return;
            }
            Statistic statistic;
            try {
                statistic = Statistic.valueOf(statisticString);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("quest " + key + "statistic type incorrect");
                return;
            }
            Statistic.Type type = statistic.getType();
            Material material = null;
            EntityType entityType = null;
            if (type == Statistic.Type.BLOCK || type == Statistic.Type.ITEM) {
                try {
                    material = Material.valueOf(materialString);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("quest " + key + " needs a valid material defined");
                    return;
                }
            } else if (type == Statistic.Type.ENTITY) {
                try {
                    entityType = EntityType.valueOf(entityString);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("quest " + key + " needs a valid entity defined");
                    return;
                }
            }
            // rewards validation (TODO)
            ConfigurationSection rewardsSection = questsSection.getConfigurationSection(key + ".levels");
            ArrayList<QuestReward> rewards = new ArrayList<>();
            for (String rewardsKey : rewardsSection.getKeys(false)) {
                ArrayList<String> lore = new ArrayList<>();
                if(rewardsSection.isList(rewardsKey + ".lore")){
                    lore.addAll(rewardsSection.getStringList(rewardsKey + ".lore"));
                } else if (rewardsSection.isString(rewardsKey + ".lore")){
                    lore.add(rewardsSection.getString(rewardsKey + ".lore"));
                }

                ArrayList<String> rewardsStrings = new ArrayList<>();
                if(rewardsSection.isList(rewardsKey + ".rewards")){
                    rewardsStrings.addAll(questsSection.getStringList(rewardsKey + ".rewards"));
                } else if (questsSection.isString(rewardsKey + ".rewards")){
                    rewardsStrings.add(questsSection.getString(rewardsKey + ".rewards"));
                }

                QuestReward reward = new QuestReward(Integer.parseInt(rewardsKey), rewardsStrings, lore);
                rewards.add(reward);
            }
            // Create the quest using config to determine constructor (no type checking tho)
            Quest quest;
            if (material != null) {
                quest = new Quest(name, loreStrings, statistic, material, rewards);
            } else if (entityType != null) {
                quest = new Quest(name, loreStrings, statistic, entityType, rewards);
            } else {
                quest = new Quest(name, loreStrings, statistic, rewards);
            }
            quests.put(key, quest);
        }
    }
    // Function to parse slots of inventory (for lists and ranges)
    public static List<Integer> parseSlotString(String input) {
        List<Integer> result = new ArrayList<>();

        // Split by comma
        String[] parts = input.split(",");

        for (String part : parts) {
            part = part.trim();
            if (part.contains("-")) {
                String[] range = part.split("-");
                try {
                    int start = Integer.parseInt(range[0]);
                    int end = Integer.parseInt(range[1]);
                    for (int i = start; i <= end; i++) {
                        result.add(i);
                    }
                } catch (NumberFormatException e) {
                    HolyCore.getInstance().getLogger().warning("Invalid range: " + part);
                }
            } else {
                try {
                    result.add(Integer.parseInt(part));
                } catch (NumberFormatException e) {
                    HolyCore.getInstance().getLogger().warning("Invalid number: " + part);
                }
            }
        }

        return result;
    }


    public static QuestsGUI getGui(Player player, String pageName) {
        HashMap<Integer, Quest> contents = new HashMap<>();
        HashMap<Integer, QuestItem> items = new HashMap<>();
        Component displayName = Component.empty();
        int invSize = 54;
        if(pagesSection.isSet(pageName)){
           if(pagesSection.getString(pageName + ".name") instanceof String name){
               displayName = TextUtils.deserialize(PlaceholderAPI.setPlaceholders(player, name));
           }
           if(pagesSection.getInt(pageName + ".size") != 0){
               invSize = pagesSection.getInt(pageName + ".size");
           }
           ConfigurationSection slotsSection = pagesSection.getConfigurationSection(pageName + ".slots");

           for(String slot : slotsSection.getKeys(false)){
               // Check if slot is int (single slot)
               int slotInt = 0;
               try {
                   slotInt = Integer.parseInt(slot);
               } catch (NumberFormatException e) {
                   slotInt = -1;
               }
               // Slot is int (1-10) or (1,2,3-6,7,8)
               if (slotInt == -1){
                   List<Integer> slots = parseSlotString(slot);
                   if (!(slots.isEmpty())) {
                       for(int slotMultiple : slots){
                           if(slotsSection.isConfigurationSection(slot)){
                               ConfigurationSection itemSection = slotsSection.getConfigurationSection(slot);
                               String name = itemSection.getString("name") == null ? "" : itemSection.getString("name");
                               List<String> lore = itemSection.getStringList("lore");
                               String material = itemSection.getString("material") == null ? "DIAMOND" : itemSection.getString("material");
                               String custom_model = itemSection.getString("custom_model") == null ? "" : itemSection.getString("custom_model");
                               List<String> clickCommands = itemSection.getStringList("click");
                               Boolean hideTooltip = itemSection.getBoolean("hide_tooltip", false);
                               QuestItem item = new QuestItem(player, material, 1, name, lore, custom_model, clickCommands, hideTooltip);
                               items.put(slotMultiple, item);
                           }
                       }
                       continue;
                   }
               }
               // Put custom item in slot
               if(slotsSection.isConfigurationSection(slot)){
                   ConfigurationSection itemSection = slotsSection.getConfigurationSection(slot);
                   String name = itemSection.getString("name") == null ? "invalid config" : itemSection.getString("name");
                   List<String> lore = itemSection.getStringList("lore");
                   String material = itemSection.getString("material") == null ? "DIAMOND" : itemSection.getString("material");
                   String custom_model = itemSection.getString("custom_model") == null ? "" : itemSection.getString("custom_model");
                   List<String> clickCommands = itemSection.getStringList("click");
                   Boolean hideTooltip = itemSection.getBoolean("hide_tooltip", false);
                   QuestItem item = new QuestItem(player, material, 1, name, lore, custom_model, clickCommands, hideTooltip);

                   items.put(slotInt, item);
               }
               // Put quest item in slot
               if(slotsSection.isString(slot)){
                   Quest quest = quests.get(slotsSection.getString(slot));
                   if(quest != null){
                       contents.put(slotInt, quest);
                   }
               }
           }
        }

        return new QuestsGUI(displayName, invSize, contents, player, items);
    }
}
