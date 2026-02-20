package org.beeholy.holyCore.utility;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.beeholy.holyCore.chat.Colors;
import org.beeholy.holyCore.chat.Gradients;
import org.beeholy.holyCore.chat.Tags;
import org.beeholy.holyCore.economy.FlyTime;
import org.beeholy.holyCore.hooks.VaultHook;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TextUtils {

    private static final Map<Character, Character> smallCapsMap = new HashMap<>();

    static {
        smallCapsMap.put('a', 'ᴀ'); smallCapsMap.put('b', 'ʙ'); smallCapsMap.put('c', 'ᴄ');
        smallCapsMap.put('d', 'ᴅ'); smallCapsMap.put('e', 'ᴇ'); smallCapsMap.put('f', 'ғ');
        smallCapsMap.put('g', 'ɢ'); smallCapsMap.put('h', 'ʜ'); smallCapsMap.put('i', 'ɪ');
        smallCapsMap.put('j', 'ᴊ'); smallCapsMap.put('k', 'ᴋ'); smallCapsMap.put('l', 'ʟ');
        smallCapsMap.put('m', 'ᴍ'); smallCapsMap.put('n', 'ɴ'); smallCapsMap.put('o', 'ᴏ');
        smallCapsMap.put('p', 'ᴘ'); smallCapsMap.put('q', 'ǫ'); smallCapsMap.put('r', 'ʀ');
        smallCapsMap.put('s', 'ѕ'); smallCapsMap.put('t', 'ᴛ'); smallCapsMap.put('u', 'ᴜ');
        smallCapsMap.put('v', 'ᴠ'); smallCapsMap.put('w', 'ᴡ'); smallCapsMap.put('x', 'x');
        smallCapsMap.put('y', 'ʏ'); smallCapsMap.put('z', 'ᴢ');
    }

    public static String toSmallText(String input) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toLowerCase().toCharArray()) {
            result.append(smallCapsMap.getOrDefault(c, c));
        }
        return result.toString();
    }


    static Tag colorsTag(final ArgumentQueue args, final Context ctx) {
        final String colorName = args.popOr("The <colors> tag requires exactly one argument, the name.").value();

        return Tag.styling(
                TextColor.fromHexString(Colors.getColor(colorName))
        );
    }

    static Tag gradientsTag(final ArgumentQueue args, final Context ctx) {
        final String gradientName = args.popOr("The <gradients> tag requires exactly one argument, the name.").value();
        // minimessage custom tag that will replace <gradients> with the inner content of the tag
        //return Tag.preProcessParsed(
        // Gradients.applyGradient(gradientName, ctx.deserialize())
        //);
        return Tag.preProcessParsed(
                Gradients.getGradient(gradientName)
        );
    }

    static Tag tagTag(final ArgumentQueue args, final Context ctx) {
        final String tagName = args.popOr("The <tag> tag requires exactly one argument, the name.").value();

        return Tag.selfClosingInserting(deserialize(Tags.getTag(tagName)));
    }

    public static Component deserializeAsPlayer(String message, Player player) {
        MiniMessage mm = MiniMessage.miniMessage();
        mm = MiniMessage.builder().tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .tag("player_tag", Tag.preProcessParsed(Tags.getPlayerTag(player)))
                        .tag("player_rank", Tag.preProcessParsed(VaultHook.getChat().getPlayerPrefix(player)))
                        .tag("player_gradient", Tag.preProcessParsed(Gradients.getPlayerGradient(player)))
                        .tag("player_color", Tag.styling(TextColor.fromHexString(Colors.getPlayerColor(player))))
                        .tag("player_flytime", Tag.preProcessParsed(FlyTime.formatSeconds(FlyTime.get(player))))
                        .tag("username", Tag.preProcessParsed(player.getName()))
                        .tag("balance", Tag.preProcessParsed(VaultHook.formatCurrencySymbol(VaultHook.getBalance(player))))
                        .tag("display_name", Tag.inserting(player.displayName()))
                        .build())
                .build();

        return mm.deserialize(PlaceholderAPI.setPlaceholders(player, message));
    }

    public static Component deserializeAsPlayer(String message, Player player, Component placeholder) {
        Component safeChat = Component.text(
                MiniMessage.miniMessage().escapeTags(
                PlainTextComponentSerializer.plainText()
                        .serialize(placeholder)
                )
        );

        MiniMessage mm = MiniMessage.builder().tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .tag("data", Tag.inserting(safeChat))
                        .tag("player_tag", Tag.preProcessParsed(Tags.getPlayerTag(player)))
                        .tag("player_rank", Tag.preProcessParsed(VaultHook.getChat().getPlayerPrefix(player)))
                        .tag("player_gradient", Tag.preProcessParsed(Gradients.getPlayerGradient(player)))
                        .tag("player_color", Tag.styling(TextColor.fromHexString(Colors.getPlayerColor(player))))
                        .tag("player_flytime", Tag.preProcessParsed(FlyTime.formatSeconds(FlyTime.get(player))))
                        .tag("username", Tag.preProcessParsed(player.getName()))
                        .tag("balance", Tag.preProcessParsed(VaultHook.formatCurrencySymbol(VaultHook.getBalance(player))))
                        .tag("display_name", Tag.inserting(player.displayName()))
                        .build())
                .build();

        return mm.deserialize(PlaceholderAPI.setPlaceholders(player, message));

    }

    public static Component deserialize(String message) {
        MiniMessage mm = MiniMessage.miniMessage();
        Component messageComponent = mm.deserialize(message);

        return messageComponent;
    }

    public static Component deserialize(String message, String data) {
        MiniMessage mm = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .tag("data", Tag.preProcessParsed(data))
                        .tag("colors", TextUtils::colorsTag)
                        .tag("gradients", TextUtils::gradientsTag)
                        .tag("tags", TextUtils::tagTag)
                        .build()
                )
                .build();
        Component messageComponent = mm.deserialize(message);
        return messageComponent;
    }

    public static Component deserialize(String message, String data, String data1) {
        MiniMessage mm = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .tag("data", Tag.preProcessParsed(data))
                        .tag("data1", Tag.preProcessParsed(data1))
                        .tag("colors", TextUtils::colorsTag)
                        .tag("gradients", TextUtils::gradientsTag)
                        .tag("tags", TextUtils::tagTag)
                        .build()
                )
                .build();
        Component messageComponent = mm.deserialize(message);
        return messageComponent;
    }
}
