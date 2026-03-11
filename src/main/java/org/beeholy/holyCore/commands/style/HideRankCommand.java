package org.beeholy.holyCore.commands.style;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.beeholy.holyCore.HolyCore;
import org.beeholy.holyCore.utility.Language;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class HideRankCommand implements BasicCommand {
    @Override
    public @Nullable String permission() {
        return "holycore.hiderank";
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();
        if(!(sender instanceof Player player)) return;
        NamespacedKey key = new NamespacedKey(HolyCore.getInstance(), "hiddenRank");
        // Handle /fly without arguments — player only
        if (args.length == 0) {
            PersistentDataContainer container = player.getPersistentDataContainer();
            if(container.has(key)){
                player.sendMessage(TextUtils.deserialize(Language.get("hiderank_show")));
                container.remove(key);
            } else {
                player.sendMessage(TextUtils.deserialize(Language.get("hiderank_hide")));
                container.set(key, PersistentDataType.BOOLEAN, true);
            }
        }

    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        return List.of();
    }
}
