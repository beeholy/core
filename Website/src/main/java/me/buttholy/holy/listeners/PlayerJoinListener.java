package me.buttholy.holy.listeners;

import me.buttholy.holy.DatabaseHelper;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {
    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event){
        Server server = event.getPlayer().getServer();
        UUID uuid = event.getPlayer().getUniqueId();
        String username = event.getPlayer().getName();
        // Add player to database if they arent already
        // Add log of UUID and username if name has changed & update current username
        // Add log of IP and UUID
        if(DatabaseHelper.findPlayer(uuid)){
            // Player exists in db
            event.getPlayer().sendMessage("Welcome back!");
            // check for username changes
        } else {
            // Player is new, add to db
            DatabaseHelper.addPlayer(uuid,username);
            event.getPlayer().sendMessage("Welcome to holy land");
        }
    }
}
