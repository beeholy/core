package org.beeholy.holyCore.model;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class QuestReward {
    private final int score;
    private final List<String> lore;
    private final List<String> rewards;

    public QuestReward(int score, List<String> rewards, List<String> lore) {
        this.rewards = rewards;
        this.lore = lore;
        this.score = score;
    }

    public List<String> getRewards() {
        return rewards;
    }

    public List<Component> getLore(Player player) {
        ArrayList<Component> lore = new ArrayList<>();
        for (String line : this.lore) {
            lore.add(TextUtils.deserialize(PlaceholderAPI.setPlaceholders(player, line)).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }
        return lore;
    }

    public int getScore() {
        return score;
    }
}
