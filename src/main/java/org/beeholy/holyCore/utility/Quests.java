package org.beeholy.holyCore.utility;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Quests {
    private static HashMap<UUID, Material> material;
    private static HashMap<UUID, Mob> mob;
    private static HashMap<UUID, Integer> blocksPlaced;
    private static HashMap<UUID, Integer> blocksMined;
    private static HashMap<UUID, Integer> mobsKilled;

    // Start quest
    public static void addQuest(Player player){

    }
    public static void getStatistic(Player player, String material){
        player.getStatistic(Statistic.MINE_BLOCK, Material.valueOf(material));
    }
}
