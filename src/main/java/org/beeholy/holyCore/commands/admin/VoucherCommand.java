package org.beeholy.holyCore.commands.admin;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
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

public class VoucherCommand implements BasicCommand {
    @Override
    public @Nullable String permission() {
        return "voucher.admin";
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
//        if (!(source.getSender() instanceof Player player)){
//            source.getSender().sendMessage("Command can only be ran by player");
//            return;
//        }
        if (args[0].isEmpty()) {
            source.getSender().sendMessage(TextUtils.deserialize(Language.get("voucher_command_usage")));
        }
        switch (args[0].toLowerCase()) {
            case "give":
                // if args 1 is player, 2 is tag name
                Player otherPlayer = Bukkit.getServer().getPlayerExact(args[1]);
                if (otherPlayer != null) {
                    if (Vouchers.hasVoucher(args[2])) {
                        // if has arg3 of amount set stack size
                        if (args.length == 4) {
                            int amount;
                            try {
                                amount = Integer.parseInt(args[3]);
                            } catch (NumberFormatException e) {
                                source.getSender().sendMessage(TextUtils.deserialize(Language.get("not_a_number")));
                                return;
                            }
                            ItemStack stack = Vouchers.getItemStack(args[2]);
                            int maxStackSize = stack.getMaxStackSize();
                            if (amount > maxStackSize) {
                                source.getSender().sendMessage(TextUtils.deserialize(Language.get("max_stack_size"), Integer.toString(maxStackSize)));
                                return;
                            }
                            stack.setAmount(amount);
                            otherPlayer.give(stack);
                        } else {
                            otherPlayer.give(Vouchers.getItemStack(args[2]));
                        }

                    }
                } else {
                    source.getSender().sendMessage(TextUtils.deserialize(Language.get("player_not_found")));
                }
                break;
            case "reload":
                source.getSender().sendMessage(TextUtils.deserialize(Language.get("config_reload")));
                Vouchers.reload();
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
            return Vouchers.getVouchers();
        }
        return List.of();
    }
}
