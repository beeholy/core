package org.beeholy.holyCore.utility;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
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

        String vanishStatus = PlaceholderAPI.setPlaceholders(player, "%essentials_vanished%");
        if(vanishStatus.equals("yes")) {
            return;
        }

        player.setPlayerListOrder(rankPriority.get(VaultHook.getPermissions().getPrimaryGroup(player)));
        String afkStatus = PlaceholderAPI.setPlaceholders(player, "%essentials_afk%");
        if (afkStatus.equals("yes")) {
            player.playerListName(TextUtils.deserializeAsPlayer("<gray>[ᴀꜰᴋ]</gray>" + Language.get("tablist_name_format"), player).decorate(TextDecoration.ITALIC));
        } else {
            player.playerListName(TextUtils.deserializeAsPlayer(Language.get("tablist_name_format"), player));
        }

    }
}
