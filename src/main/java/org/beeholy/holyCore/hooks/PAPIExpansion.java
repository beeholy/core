package org.beeholy.holyCore.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.beeholy.holyCore.HolyCore;
import org.beeholy.holyCore.chat.Colors;
import org.beeholy.holyCore.chat.Gradients;
import org.beeholy.holyCore.chat.Tags;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class PAPIExpansion extends PlaceholderExpansion {

    private final HolyCore plugin; //

    public PAPIExpansion(HolyCore plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors()); //
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "holycore";
    }

    @Override
    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion(); //
    }

    @Override
    public boolean persist() {
        return true; //
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.toLowerCase().startsWith("gradient:")) {
            List<String> param = Arrays.stream(params.split(":")).toList();
            if (param.size() == 2)
                return Gradients.getGradient(param.get(1)); //
            if (param.size() == 3) {
                return LegacyComponentSerializer
                        .legacySection()
                        .serialize(MiniMessage.miniMessage()
                                .deserialize(Gradients.getGradient(param.get(1)) + PlaceholderAPI.setBracketPlaceholders(player, param.get(2)) + "</gradient>"));
            }
        }

        if (params.toLowerCase().startsWith("tag:")) {
            String tagName = params.substring("tag:".length());
            return LegacyComponentSerializer
                    .legacySection()
                    .serialize(MiniMessage.miniMessage()
                            .deserialize(Tags.getTag(tagName)));
        }

        if (params.toLowerCase().startsWith("color:")) {
            String gradientName = params.substring("color:".length());
            return Colors.getColor(gradientName);
        }

        if(params.toLowerCase().equals("gradient")){
            if(player.isOnline()) {
                return Gradients.getPlayerGradient((Player) player);
            } else {
                return "";
            }
        }

        if(params.toLowerCase().equals("envoy")){
            String nextEnvoy = PlaceholderAPI.setPlaceholders(player, "%axenvoy_nextstart_default%");
            String amountLeft = PlaceholderAPI.setPlaceholders(player, "%axenvoy_remaining_default%");
            if(nextEnvoy.equals("running")) {
                return amountLeft;
            } else {
                return nextEnvoy;
            }
        }

        return null;
    }
}