package org.beeholy.holyCore.model;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.beeholy.holyCore.HolyCore;
import org.beeholy.holyCore.utility.Quests;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Statistic;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class Quest {
    private final Statistic statistic;
    private Material material;
    private EntityType entityType;
    private final String displayName;
    // <level (e.g. 256 for 256 blocks mined), list<rewards>>
    private final ArrayList<QuestReward> rewards;
    private final ArrayList<String> loreString = new ArrayList<>();
    private final NamespacedKey key;

    // Constructor with material
    public Quest(String name, List<String> lore, Statistic statistic, Material material, ArrayList<QuestReward> rewards) {
        this.statistic = statistic;
        this.displayName = name;
        this.material = material;
        this.rewards = rewards;
        this.loreString.addAll(lore);
        this.key = new NamespacedKey(HolyCore.getInstance(), "quest_" + this.statistic.toString().toLowerCase() + "_" + this.material.toString().toLowerCase());
    }

    // Constructor overload with entity
    public Quest(String name, List<String> lore, Statistic statistic, EntityType entityType, ArrayList<QuestReward> rewards) {
        this.statistic = statistic;
        this.displayName = name;
        this.entityType = entityType;
        this.rewards = rewards;
        this.loreString.addAll(lore);
        this.key = new NamespacedKey(HolyCore.getInstance(), "quest_" + this.statistic.toString().toLowerCase() + "_" + this.entityType.toString().toLowerCase());

    }

    // Constructor overload with just stat
    public Quest(String name, List<String> lore, Statistic statistic, ArrayList<QuestReward> rewards) {
        this.statistic = statistic;
        this.displayName = name;
        this.material = null;
        this.entityType = null;
        this.rewards = rewards;
        this.loreString.addAll(lore);
        this.key = new NamespacedKey(HolyCore.getInstance(), "quest_" + this.statistic.toString().toLowerCase());
    }


    public Material getMaterial() {
        return this.material;
    }

    public Statistic getStatistic() {
        return statistic;
    }

    public ArrayList<QuestReward> getRewards() {
        return rewards;
    }

    public void giveReward(Player player, int level){
        for(QuestReward reward : rewards) {
            if(reward.getScore() == level){
                List<String> rewardsStrings = reward.getRewards();
                for(String line : rewardsStrings) {
                    if(line.startsWith("(console) ")){
                        CommandSender commandSender = Bukkit.getConsoleSender();
                        Bukkit.getServer().dispatchCommand(commandSender, PlaceholderAPI.setPlaceholders(player,line.replace("(console) ", "")));
                    } else if(line.startsWith("(player) ")){
                        Bukkit.getServer().dispatchCommand(player, PlaceholderAPI.setPlaceholders(player,line.replace("(player) ", "")));
                    }
                }
                break;
            }
        }
    }

    public ItemStack getItemStack(Player player) {
        ItemStack itemStack;
        if (getMaterial() == null) {
            itemStack = ItemStack.of(Material.NAME_TAG);
        } else {
            itemStack = ItemStack.of(getMaterial());
        }

        ItemMeta meta = itemStack.getItemMeta();
        ArrayList<Component> lore = new ArrayList<>();

        ArrayList<Integer> rewardsLevels = new ArrayList<>();
        getRewards().forEach((QuestReward q) -> {
            rewardsLevels.add(q.getScore());
        });

        int playerLevel = getLevel(player);
        int playerStatistic = getStatistic(player);
        int rewardQuantity;

        if (playerLevel == rewardsLevels.size()) {
            Component nameComponent = (TextUtils
                    .deserialize(displayName)
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

            meta.displayName(nameComponent.append(TextUtils.deserialize(Quests.getCompleted_tag())));
            for(String line : Quests.getCompleted_lore()) {
                lore.add(TextUtils.deserialize(PlaceholderAPI
                                .setPlaceholders(player, line))
                                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
            }
            meta.setEnchantmentGlintOverride(true);
            meta.lore(lore);
            itemStack.setItemMeta(meta);
            return itemStack;
        } else {
            rewardQuantity = rewardsLevels.get(getLevel(player));
            Component nameComponent = (TextUtils
                    .deserialize(displayName, String.valueOf(rewardQuantity))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
            meta.displayName(nameComponent);
        }

        // Logic for lore display
        for(String line : loreString) {
            lore.add(TextUtils.deserialize(PlaceholderAPI
                    .setPlaceholders(player, line.replace("<data>", String.valueOf(rewardQuantity))))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }
        if (playerLevel < 1) {
            lore.addAll(rewards.getFirst().getLore(player));
            if (playerStatistic >= rewardQuantity) {
                lore.add(TextUtils.deserialize(Quests.getClaim_lore()).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                meta.setEnchantmentGlintOverride(true);
            } else {
                lore.add(Component.text(playerStatistic + " / " + rewardQuantity));
            }
        } else if (playerLevel == rewardsLevels.size()) {
            // completed all
        } else {
            lore.addAll(rewards.getFirst().getLore(player));

            if (playerStatistic >= rewardQuantity) {
                lore.add(TextUtils.deserialize(Quests.getClaim_lore()).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                meta.setEnchantmentGlintOverride(true);
            } else {
                String status_lore = Quests.getStatus_lore();
                status_lore = status_lore.replace("<1>", String.valueOf(playerStatistic));
                status_lore = status_lore.replace("<2>", String.valueOf(rewardQuantity));
                lore.add(TextUtils.deserialize(status_lore).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
            }
        }
        meta.lore(lore);


        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public int getLevel(Player player) {
        // returns level or -1 if quest doesn't exist on player pdc
        int level = 0;
        if (!(player.getPersistentDataContainer().get(key, PersistentDataType.INTEGER) == null)) {
            level = player.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        }
        return level;
    }

    public void setLevel(Player player, int level) {
        player.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, level);
    }

    public int getStatistic(Player player) {
        if (material != null) {
            return player.getStatistic(statistic, material);
        }
        if (entityType != null) {
            return player.getStatistic(statistic, entityType);
        }
        return player.getStatistic(statistic);
    }
}
