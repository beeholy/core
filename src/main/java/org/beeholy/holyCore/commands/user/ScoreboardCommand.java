package org.beeholy.holyCore.commands.user;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.beeholy.holyCore.utility.Scoreboard;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class ScoreboardCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();

        if(!(sender instanceof Player player)) return;

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
