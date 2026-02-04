package org.beeholy.holyCore.commands.admin;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.beeholy.holyCore.items.ItemFactory;
import org.beeholy.holyCore.items.Vouchers;
import org.beeholy.holyCore.utility.Language;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ItemCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (args[0].equals("give") && args.length >= 3) {
            Player otherPlayer = Bukkit.getPlayerExact(args[1]);
            if (otherPlayer != null) {
                String item = args[2];
                if (item.equals("spawner_pick")) {
                    otherPlayer.give(ItemFactory.createSpawnerPick());
                }
                if (item.equals("capture_egg")) {
                    int amount;
                    if(args.length >= 4) {
                        try {
                            amount = Integer.parseInt(args[3]);
                        } catch (NumberFormatException e) {
                            source.getSender().sendMessage(TextUtils.deserialize(Language.get("not_a_number")));
                            return;
                        }
                        if (args.length == 5) {
                            if(args[4].equals("true")) {
                                otherPlayer.give(ItemFactory.createMobCaptureEgg(true));
                            }
                        } else {
                            otherPlayer.give(ItemFactory.createMobCaptureEgg(false));
                        }
                    }
                }
            }
        }
    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        if (args.length == 0 || args.length == 1) {
            return List.of("give");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return Bukkit.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            return List.of("spawner_pick", "capture_egg");
        }
        if (args.length == 4 && args[2].equalsIgnoreCase("capture_egg")) {
            return List.of("1");
        }
        if (args.length == 5 && args[2].equalsIgnoreCase("capture_egg")) {
            return List.of("true", "false");
        }
        return List.of();
    }

    @Override
    public @Nullable String permission() {
        return "holycore.admin";
    }
}
