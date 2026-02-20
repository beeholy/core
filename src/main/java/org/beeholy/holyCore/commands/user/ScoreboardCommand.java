package org.beeholy.holyCore.commands.user;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.beeholy.holyCore.HolyCore;
import org.beeholy.holyCore.economy.FlyTime;
import org.beeholy.holyCore.utility.Language;
import org.beeholy.holyCore.utility.Scoreboard;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreboardCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();

        if(!(sender instanceof Player player)) return;
        // Handle /fly without arguments — player only
        if (args.length == 0) {
            if(Scoreboard.hasScore(player)){
                Scoreboard.hideScore(player);
            } else {
                Scoreboard.showScore(player);
            }
        }

    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        return List.of();
    }
}
