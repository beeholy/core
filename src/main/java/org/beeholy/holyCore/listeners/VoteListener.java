package org.beeholy.holyCore.listeners;

import org.beeholy.holyCore.HolyCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import com.vexsoftware.votifier.model.VotifierEvent;
import com.vexsoftware.votifier.model.Vote;

public class VoteListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onVotifierEvent(VotifierEvent event) {
        Vote vote = event.getVote();
        //HolyCore.getInstance().getLogger().info("vote: " + vote.toString());
        Player player = Bukkit.getServer().getPlayerExact(vote.getUsername());
        if(player != null) {
            HolyCore.getInstance().getLogger().info("Online player voted");
            return;
        }
        // runnable
        Bukkit.getScheduler().runTaskAsynchronously(HolyCore.getInstance(), () -> {
            try {
                OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(vote.getUsername());
                if (offlinePlayer.hasPlayedBefore()) {
                    HolyCore.getInstance().getLogger().info("Offline player vote");
                    // Increase player offline vote record
                }
            } catch (Exception e) {
                HolyCore.getInstance().getLogger().info("Vote with a username that doesn't exist");
            }
        });

        // Send thank you

        // Let everyone know they voted

        // Run reward(s)

        /*
         * Process Vote record as you see fit
         */
    }
}