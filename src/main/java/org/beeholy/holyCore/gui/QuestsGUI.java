package org.beeholy.holyCore.gui;

import net.kyori.adventure.text.Component;
import org.beeholy.holyCore.model.Quest;
import org.beeholy.holyCore.model.QuestItem;
import org.beeholy.holyCore.model.QuestReward;
import org.beeholy.holyCore.utility.Quests;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class QuestsGUI extends Menu {

    private final HashMap<Integer, Quest> quests;
    private final HashMap<Integer, QuestItem> items;
    private final Player player;

    public QuestsGUI(Component title, int size, HashMap<Integer, Quest> quests, Player player, HashMap<Integer, QuestItem> items) {
        super(title, size);
        this.quests = quests;
        this.player = player;
        this.items = items;
        reloadInventory();
    }

    // Display Quests
    public void reloadInventory() {
        for (int key : quests.keySet()) {
            Quest quest = quests.get(key);
            ItemStack itemStack = quest.getItemStack(player);
            getInventory().setItem(key, itemStack);
        }
        for (int key : items.keySet()) {
            ItemStack item = items.get(key);
            getInventory().setItem(key, item);
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        super.handleClick(event);
        event.setCancelled(true);

        QuestItem item = items.get(event.getRawSlot());
        if(item != null){
            for(String line : item.getClickCommands()) {
                if(line.startsWith("(console) ")){
                    CommandSender commandSender = Bukkit.getConsoleSender();
                    Bukkit.getServer().dispatchCommand(commandSender, line.replace("(console) ", ""));
                } else if(line.startsWith("(player) ")){
                    Bukkit.getServer().dispatchCommand(player, line.replace("(player) ", ""));
                }
            }
        }

        Quest quest = quests.get(event.getRawSlot());
        if (quest != null) {
            ArrayList<Integer> rewardsLevels = new ArrayList<>();
            // Claim rewards
            quest.getRewards().forEach((QuestReward q) -> {
                rewardsLevels.add(q.getScore());
            });
            int playerLevel = quest.getLevel(player);
            int playerStatistic = quest.getStatistic(player);

            // rewards player gets = player level + 1
            if (playerLevel == rewardsLevels.size()) return;

            if (playerLevel < 1) {
                if (playerStatistic >= rewardsLevels.get(0)) {
                    // First level award
                    quest.giveReward(player, rewardsLevels.get(0));
                    quest.setLevel(player, 1);
                }
            } else if (playerStatistic >= rewardsLevels.get(playerLevel)) {
                // Reward
                quest.giveReward(player, rewardsLevels.get(playerLevel));
                quest.setLevel(player, playerLevel + 1);
            }
        }
        reloadInventory();
    }
}
