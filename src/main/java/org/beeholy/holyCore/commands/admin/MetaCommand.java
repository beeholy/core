package org.beeholy.holyCore.commands.admin;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

public class MetaCommand implements BasicCommand {

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        Player player = Bukkit.getServer().getPlayerExact(args[0]);
        if(player != null){
            if(args.length < 2) return;
            if(args[1].equals("list")){
                Set<NamespacedKey> keys = player.getPersistentDataContainer().getKeys();
                for(NamespacedKey key : keys){
                    player.sendMessage(key.getNamespace() + ":" + key.getKey() + " = " + key.value());
                }
            }
            if(args.length < 3) return;
            if(args[1].equals("set")) {
                PersistentDataContainer container = player.getPersistentDataContainer();
                container.remove(NamespacedKey.fromString(args[2]));
            }
        } else {
            source.getSender().sendMessage("Player isn't online");
        }
    }

    @Override
    public Collection<String> suggest(CommandSourceStack commandSourceStack, String[] args) {
        return BasicCommand.super.suggest(commandSourceStack, args);
    }

    @Override
    public @Nullable String permission() {
        return "holycore.admin";
    }
}
