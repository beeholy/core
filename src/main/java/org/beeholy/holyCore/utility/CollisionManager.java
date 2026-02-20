package org.beeholy.holyCore.utility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;


    public class CollisionManager {

        private static final String TEAM_NAME = "NO_COLLISION";

        public static void apply(Player player) {
            org.bukkit.scoreboard.Scoreboard board =
                    Bukkit.getScoreboardManager().getMainScoreboard();

            Team team = board.getTeam(TEAM_NAME);
            if (team == null) {
                team = board.registerNewTeam(TEAM_NAME);
                team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            }

            team.addEntry(player.getName());
        }

        public static void remove(Player player) {
            org.bukkit.scoreboard.Scoreboard board =
                    Bukkit.getScoreboardManager().getMainScoreboard();

            Team team = board.getTeam(TEAM_NAME);
            if (team != null) {
                team.removeEntry(player.getName());
            }
        }
    }
