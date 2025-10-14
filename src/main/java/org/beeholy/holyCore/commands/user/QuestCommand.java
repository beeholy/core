package org.beeholy.holyCore.commands.user;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.beeholy.holyCore.utility.Quests;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.entity.Player;

import java.util.Collection;

public class QuestCommand implements BasicCommand {

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getSender() instanceof Player player)) {
            return;
        }
        if(args.length == 0) return;
        if(Quests.getPagesSection().isSet(args[0])) {
            player.openInventory(Quests.getGui(player, args[0]).getInventory());
        } else {
            player.sendMessage(TextUtils.deserialize("Quest menu does not exist"));
        }
    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        return BasicCommand.super.suggest(source, args);
    }
}
