package org.beeholy.holyCore.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.beeholy.holyCore.HolyCore;
import org.beeholy.holyCore.economy.FlyTime;
import org.beeholy.holyCore.utility.Language;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FlyCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();

        // Handle /fly without arguments — player only
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use /fly with no arguments.");
                return;
            }

            if (player.getAllowFlight()) {
                player.setAllowFlight(false);
                player.sendMessage(TextUtils.deserialize(Language.get("fly_disabled")));
            } else if (FlyTime.get(player) > 0 || player.hasPermission("fly.unlimited")) { // or has perm fly.unlimited
                player.setAllowFlight(true);
                player.setFlying(true);
                player.sendMessage(TextUtils.deserialize(Language.get("fly_enabled")));
            } else {
                player.sendMessage(TextUtils.deserialize(Language.get("fly_no_time")));
            }
            return;
        }

        String subcommand = args[0].toLowerCase();

        if (List.of("give", "take", "set").contains(subcommand)) {
            if (!sender.hasPermission("fly.admin")) {
                sender.sendMessage(TextUtils.deserialize(Language.get("perms_invalid")));
                return;
            }

            if (args.length != 3) {
                sender.sendMessage(TextUtils.deserialize(Language.get("fly_command_usage")));
                return;
            }

            Player target = Bukkit.getServer().getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage(TextUtils.deserialize(Language.get("player_not_found")));
                return;
            }

            try {
                int amount = Integer.parseInt(args[2]);
                switch (subcommand) {
                    case "give" -> FlyTime.give(target, amount);
                    case "take" -> FlyTime.take(target, amount);
                    case "set" -> FlyTime.set(target, amount);
                }
                sender.sendMessage("Fly time updated for " + target.getName());
            } catch (NumberFormatException ex) {
                sender.sendMessage(TextUtils.deserialize(Language.get("not_a_number")));
            }
            return;
        }

        // /fly bal — player only
        if (subcommand.equals("bal")) {
            if (args.length == 2) {
                Bukkit.getScheduler().runTaskAsynchronously(HolyCore.getInstance(), () -> {
                    try {
                        OfflinePlayer otherPlayer = Bukkit.getOfflinePlayer(args[1]);
                        if (otherPlayer == null) {
                            sender.sendMessage(TextUtils.deserialize(Language.get("player_not_found")));
                            return;
                        }
                        int flyTime = FlyTime.get(otherPlayer);
                        sender.sendMessage(TextUtils.deserialize(Language.get("fly_bal_others"), otherPlayer.getName(), FlyTime.formatSeconds(flyTime)));
                    } catch (Exception ex) {
                        sender.sendMessage(TextUtils.deserialize(Language.get("player_not_found")));
                    }

                    });
                return;
            }
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use /fly bal.");
                return;
            }

            player.sendMessage(TextUtils.deserializeAsPlayer(Language.get("fly_bal_message"), player));
            return;
        }
        // /fly pay
        if(subcommand.equals("pay")){
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use /fly bal.");
                return;
            }
            if(args.length == 3){
                Player target = Bukkit.getServer().getPlayerExact(args[1]);
                if (target == null) {
                    sender.sendMessage(TextUtils.deserialize(Language.get("player_not_found")));
                    return;
                }
                if(FlyTime.pay(player, target, args[2])){
                    sender.sendMessage(TextUtils.deserialize(Language.get("fly_paid"), target.getName(), args[2]));
                    return;
                }
            }
        }
        sender.sendMessage(TextUtils.deserialize(Language.get("fly_command_usage")));
    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        if (args.length == 0 || args.length == 1) {
            if(source.getSender().hasPermission("fly.admin"))
                return List.of("give", "take", "set", "bal", "pay");
            return(List.of("bal", "pay"));
        }
        if (args.length == 2) {
            return Bukkit.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
