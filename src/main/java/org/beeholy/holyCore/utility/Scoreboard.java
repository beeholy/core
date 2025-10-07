package org.beeholy.holyCore.utility;

import io.papermc.paper.scoreboard.numbers.NumberFormat;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.beeholy.holyCore.HolyCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author crisdev333
 */
public class Scoreboard {

    private static final HashMap<UUID, Scoreboard> players = new HashMap<>();
    private static String title;
    private static List<String> slots;

    static {
        reload();
    }

    private final org.bukkit.scoreboard.Scoreboard scoreboard;
    private final Objective sidebar;
    private final MiniMessage mm = MiniMessage.miniMessage();

    private Scoreboard(Player player) {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        sidebar = scoreboard.registerNewObjective("sidebar", "dummy");
        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        sidebar.numberFormat(NumberFormat.blank());
        // Create Teams
        for (int i = 1; i <= 15; i++) {
            Team team = scoreboard.registerNewTeam("SLOT_" + i);
            team.addEntry(genEntry(i));
        }
        player.setScoreboard(scoreboard);
        players.put(player.getUniqueId(), this);
    }

    public static void reload() {
        File scoreboardFile;
        FileConfiguration scoreboardConfig;
        HolyCore plugin = HolyCore.getInstance();

        scoreboardFile = new File(plugin.getDataFolder(), "scoreboard.yml");

        if (!scoreboardFile.exists()) {
            scoreboardFile.getParentFile().mkdirs();
            plugin.saveResource("scoreboard.yml", false); // copies from jar if present
        }

        scoreboardConfig = YamlConfiguration.loadConfiguration(scoreboardFile);
        // From config, load into static hashmap
        title = scoreboardConfig.getString("title");
        slots = scoreboardConfig.getStringList("slots");
    }

    public static boolean hasScore(Player player) {
        return players.containsKey(player.getUniqueId());
    }

    public static Scoreboard createScore(Player player) {
        return new Scoreboard(player);
    }

    public static Scoreboard getByPlayer(Player player) {
        return players.get(player.getUniqueId());
    }

    public static Scoreboard removeScore(Player player) {
        return players.remove(player.getUniqueId());
    }

    public static void updateScoreboard(Player player) {
        if (hasScore(player)) {
            Scoreboard helper = getByPlayer(player);
            helper.setTitle(PlaceholderAPI.setPlaceholders(player, title));
            for (int i = 0; i < slots.size(); i++) {
                helper.setSlot(15 - i, PlaceholderAPI.setPlaceholders(player, slots.get(i)));
            }
        }
    }

    public void setTitle(String title) {
        sidebar.displayName(mm.deserialize(title));
    }

    public void setSlot(int slot, String text) {
        Team team = scoreboard.getTeam("SLOT_" + slot);
        String entry = genEntry(slot);
        if (!scoreboard.getEntries().contains(entry)) {
            sidebar.getScore(entry).setScore(slot);
        }

        team.prefix(mm.deserialize(text));
    }

    public void removeSlot(int slot) {
        String entry = genEntry(slot);
        if (scoreboard.getEntries().contains(entry)) {
            scoreboard.resetScores(entry);
        }
    }

    private String genEntry(int slot) {
        return ChatColor.values()[slot].toString();
    }

}