package org.beeholy.holyCore.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.beeholy.holyCore.items.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.Collection;

public class ItemCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if(args[0].equals("give") && args.length == 3){
            Player otherPlayer = Bukkit.getPlayerExact(args[1]);
            if (otherPlayer != null){
                String item = args[2];
                if(item.equals("spawner_pick")){
                    otherPlayer.give(ItemFactory.createSpawnerPick());
                }
            }
        }
    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        return BasicCommand.super.suggest(source, args);
    }

    @Override
    public @Nullable String permission() {
        return "itemfactory.admin";
    }
}
