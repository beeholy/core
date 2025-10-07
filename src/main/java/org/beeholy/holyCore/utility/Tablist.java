package org.beeholy.holyCore.utility;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.beeholy.holyCore.hooks.LPHook;
import org.beeholy.holyCore.hooks.VaultHook;
import org.bukkit.entity.Player;

import java.util.Map;

public class Tablist {
    static final Map<String, Integer> rankPriority = LPHook.getRankPriority();
    LuckPerms luckPerms = LuckPermsProvider.get();

    public static void send(Player player) {
        MiniMessage mm = MiniMessage.miniMessage();
        final Component header = TextUtils.deserialize(PlaceholderAPI.setPlaceholders(player, Language.get("tablist_header")));
        final Component footer = TextUtils.deserialize(PlaceholderAPI.setPlaceholders(player, Language.get("tablist_footer")));
        player.sendPlayerListHeaderAndFooter(header, footer);
        player.setPlayerListOrder(rankPriority.get(VaultHook.getPermissions().getPrimaryGroup(player)));
        player.playerListName(TextUtils.deserializeAsPlayer(Language.get("tablist_name_format"), player));
    }
}
