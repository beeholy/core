package org.beeholy.holyCore.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.beeholy.holyCore.HolyCore;
import org.beeholy.holyCore.economy.FlyTime;
import org.beeholy.holyCore.utility.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final MiniMessage mm = MiniMessage.miniMessage();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // cache player into memory

        Player player = event.getPlayer();
        FlyTime.addPlayer(player.getUniqueId().toString());
        // Welcome back message, run async due to db request
        Bukkit.getScheduler().runTaskAsynchronously(HolyCore.getInstance(), () -> {
            String messageString = Language.get("welcome_message");
            // Add to users table in database if not joined before
            Boolean firstJoin = DBManager.getInstance().createUser(event.getPlayer().getName(), event.getPlayer().getUniqueId());
            if (firstJoin)
                messageString = Language.get("first_join_message");

            Component message = TextUtils.deserializeAsPlayer(messageString, player);
            player.sendMessage(message);
        });

        // Set up scoreboard
        Scoreboard helper = Scoreboard.createScore(player);
        Scoreboard.updateScoreboard(player);

        Tablist.send(player);

        // Set up no collision
        CollisionManager.apply(event.getPlayer());
    }
}
