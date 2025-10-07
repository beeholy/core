package org.beeholy.holyCore.economy;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.beeholy.holyCore.HolyCore;
import org.beeholy.holyCore.utility.Language;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlyTime {

    private static HashMap<String, Integer> balances;
    private static File file;
    private static FileConfiguration config;
    private static final HolyCore plugin = HolyCore.getInstance();

    public static void setup () {

        file = new File(HolyCore.getInstance().getDataFolder(), "flytime.yml");
        balances = new HashMap<>();

        if(!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource("flytime.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(file);

        for(String key : config.getKeys(false)){
            Integer value = config.getInt(key);
            if (value != null) {
                balances.put(key, value);
            }
        }
    }
    public static boolean addPlayer(String uuid){
        if(config.get(uuid) == null){
            config.set(uuid, 0);
            save();
            plugin.getLogger().info("Added new player flytime uuid: " + uuid);
            return true;
        }
        return false;
    }

    private static void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean give(Player player, int amount){
        String uuid = player.getUniqueId().toString();
        Integer balance = config.getInt(uuid);
        if(balance != null){
            config.set(uuid, balance + amount);
            save();
            player.sendMessage(TextUtils.deserialize(Language.get("fly_received"), formatSeconds(amount)));
            return true;
        }
        return false;
    }

    public static boolean take(Player player, int amount){
        String uuid = player.getUniqueId().toString();
        Integer balance = config.getInt(uuid);
        if(balance != null){
            if(balance <= amount) {
                config.set(uuid, 0);
            } else {
                config.set(uuid, balance - amount);
            }
            save();
            return true;
        }
        return false;
    }
    public static boolean pay(Player from, Player to, String timeString) {
        if(from == to) return false;
        String uuidFrom = from.getUniqueId().toString();
        int balanceFrom = config.getInt(uuidFrom);
        int amount = parseTime(timeString);

        if(amount == -1) return false;

        if(balanceFrom >= amount){
            to.sendMessage(TextUtils.deserialize(Language.get("fly_received_from"), from.getName()));
            return take(from, amount) && give(to, amount);
        }
        return false;
    }

    public static boolean set(Player player, int amount){
        String uuid = player.getUniqueId().toString();
        Integer balance = config.getInt(uuid);
        if(balance != null){
            config.set(uuid, amount);
            save();
            return true;
        }
        return false;
    }
    public static Integer get(OfflinePlayer player) {
        Integer balance = config.getInt(player.getUniqueId().toString());
        if(balance != null)
            return balance;
        return -1;
    }

    private static void showTitle(Player player) {
        String playerTime = formatSeconds(get(player));
        Component mainTitle = TextUtils.deserializeAsPlayer(Language.get("flytime_title"), player);
        Component subtitle = TextUtils.deserializeAsPlayer(Language.get("flytime_subtitle"), player);

        Title title = Title.title(mainTitle, subtitle);

        player.showTitle(title);
    }
    public static void tick(Player player) {
        // check for perm fly.unlimited
        if(player.hasPermission("fly.unlimited")) return;
        if(player.getAllowFlight()){
            if(get(player) == 0) {
                player.setAllowFlight(false);
            }
            if(get(player) == 10 || get(player) == 30 || get(player) == 300) {
                // 10 second warning
                showTitle(player);
            }
            player.sendActionBar(TextUtils.deserializeAsPlayer(Language.get("flytime_action_bar"),  player));
            take(player, 1);
        }
    }

    public static String formatSeconds(int totalSeconds) {
        // Format into hours minutes and seconds
        int days = totalSeconds / 86400;
        int hours = (totalSeconds % 86400) / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        StringBuilder sb = new StringBuilder();
        if(days > 0) sb.append(days).append("d");
        if (hours > 0) sb.append(hours).append("h");
        if (minutes > 0) sb.append(minutes).append("m");
        if (seconds > 0 || sb.length() == 0) sb.append(seconds).append("s");

        return sb.toString();
    }

    public static int parseTime(String timeString) {
        int totalSeconds = 0;

        if (timeString == null || timeString.trim().isEmpty()) {
            return -1;
        }

        timeString = timeString.toLowerCase();

        try {
            Matcher matcher = Pattern
                    .compile("(\\d+)([dhms])")
                    .matcher(timeString);

            boolean found = false;

            while (matcher.find()) {
                found = true;
                int value = Integer.parseInt(matcher.group(1));
                switch (matcher.group(2)) {
                    case "d": totalSeconds += value * 86400; break;
                    case "h": totalSeconds += value * 3600; break;
                    case "m": totalSeconds += value * 60; break;
                    case "s": totalSeconds += value; break;
                    default:
                        return -1;
                }
            }

            if (!found) {
                return -1;
            }

        } catch (NumberFormatException e) {
            return -1;
        }

        return totalSeconds;
    }

}
