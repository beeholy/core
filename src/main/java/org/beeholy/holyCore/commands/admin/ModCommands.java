package org.beeholy.holyCore.commands.admin;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.beeholy.holyCore.economy.FlyTime;
import org.beeholy.holyCore.events.PunishEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ModCommands {
    public LiteralCommandNode<CommandSourceStack> banCommand =
            Commands.literal("ban")
                    .requires(sender -> sender.getSender().hasPermission("holycore.ban"))
                    .then(Commands.argument("profile", ArgumentTypes.playerProfiles())
                            .then(Commands.argument("duration", StringArgumentType.string())
                                    .then(Commands.argument("reason", StringArgumentType.greedyString())
                                            .executes(ModCommands::handleBan)
                                    )
                            )
                    )
                    .build();
    public LiteralCommandNode<CommandSourceStack> muteCommand =
            Commands.literal("mute")
                    .requires(sender -> sender.getSender().hasPermission("holycore.mute"))
                    .then(Commands.argument("profile", ArgumentTypes.playerProfiles())
                            .then(Commands.argument("duration", StringArgumentType.string())
                                    .then(Commands.argument("reason", StringArgumentType.greedyString())
                                            .executes(ModCommands::handleMute)
                                    )
                            )
                    )
                    .build();
    public LiteralCommandNode<CommandSourceStack> kickCommand =
            Commands.literal("kick")
                    .requires(sender -> sender.getSender().hasPermission("holycore.kick"))
                    .then(Commands.argument("profile", ArgumentTypes.playerProfiles())
                            .then(Commands.argument("reason", StringArgumentType.greedyString())
                                    .executes(ModCommands::handleKick)
                            )
                    )
                    .build();
    public LiteralCommandNode<CommandSourceStack> statCommand =
            Commands.literal("stat")
                    .requires(sender -> sender.getSender().hasPermission("holycore.statistic"))
                    .then(Commands.argument("target", ArgumentTypes.player())
                            .then(Commands.argument("statistic", StringArgumentType.string())
                                    .then(Commands.argument("block", StringArgumentType.string())
                                            .then(Commands.argument("value", IntegerArgumentType.integer())
                                                    .executes(ModCommands::handleStat)
                                            )
                                    )
                            )
                    )
                    .build();

    private static int handleBan(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        final PlayerProfileListResolver profilesResolver = ctx.getArgument("profile", PlayerProfileListResolver.class);
        final Collection<PlayerProfile> foundProfiles = profilesResolver.resolve(ctx.getSource());
        for (final PlayerProfile profile : foundProfiles) {
            String nameString = profile.getName();
            String durationString = ctx.getArgument("duration", String.class);
            String reasonString = ctx.getArgument("reason", String.class);
            if (FlyTime.parseTime(durationString) == -1) return 0;
            CommandSender sender = ctx.getSource().getSender();

            Bukkit.getServer().dispatchCommand(sender, "essentials:tempban " + nameString + " " + durationString + " " + reasonString);
            // Call event for punisher / notifier or hook into discordsrv
            Bukkit.getPluginManager().callEvent(new PunishEvent(profile.getName(), sender.getName(), "banned", durationString, reasonString));
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int handleMute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        final PlayerProfileListResolver profilesResolver = ctx.getArgument("profile", PlayerProfileListResolver.class);
        final Collection<PlayerProfile> foundProfiles = profilesResolver.resolve(ctx.getSource());
        for (final PlayerProfile profile : foundProfiles) {
            String nameString = profile.getName();
            String durationString = ctx.getArgument("duration", String.class);
            String reasonString = ctx.getArgument("reason", String.class);
            if (FlyTime.parseTime(durationString) == -1) return 0;
            CommandSender sender = ctx.getSource().getSender();

            Bukkit.getServer().dispatchCommand(sender, "essentials:mute " + nameString + " " + durationString + " " + reasonString);
            // Call event for punisher / notifier or hook into discordsrv
            Bukkit.getPluginManager().callEvent(new PunishEvent(profile.getName(), sender.getName(), "muted", durationString, reasonString));
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int handleKick(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        final PlayerProfileListResolver profilesResolver = ctx.getArgument("profile", PlayerProfileListResolver.class);
        final Collection<PlayerProfile> foundProfiles = profilesResolver.resolve(ctx.getSource());
        for (final PlayerProfile profile : foundProfiles) {
            String nameString = profile.getName();
            String reasonString = ctx.getArgument("reason", String.class);

            CommandSender sender = ctx.getSource().getSender();
            Bukkit.getServer().dispatchCommand(sender, "essentials:kick " + nameString + " " + reasonString);
            // Call event for punisher / notifier or hook into discordsrv
            Bukkit.getPluginManager().callEvent(new PunishEvent(profile.getName(), sender.getName(), "kicked", null, reasonString));
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int handleStat(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("target", PlayerSelectorArgumentResolver.class);
        final Player target = targetResolver.resolve(ctx.getSource()).getFirst();
        String statistic = ctx.getArgument("statistic", String.class);
        String material = ctx.getArgument("block", String.class);
        int value = ctx.getArgument("value", Integer.class);

        target.setStatistic(Statistic.valueOf(statistic), Material.getMaterial(material), value);

        return Command.SINGLE_SUCCESS;
    }
}
