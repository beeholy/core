package org.beeholy.holyCore.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.entity.PlayerGiveResult;
import org.beeholy.holyCore.HolyCore;
import org.beeholy.holyCore.gui.RewardsMenu;
import org.beeholy.holyCore.model.Crate;
import org.beeholy.holyCore.utility.Crates;
import org.beeholy.holyCore.utility.DatabaseManager;
import org.beeholy.holyCore.utility.Language;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class CratesCommand implements BasicCommand {

     public void giveKey(Player player, String crateName, int amount){
        Crate crate = Crates.getCrates().get(crateName);
        ItemStack key = crate.getKeyItem();
        key.setAmount(amount);
        PlayerGiveResult given = player.give(List.of(key), false);
        player.sendMessage(TextUtils.deserialize(Language.get("crate_keys_given"), String.valueOf(amount), crate.getName()));
        Collection<ItemStack> leftover = given.leftovers();
        if(!leftover.isEmpty()){
            ItemStack leftoverStack = (ItemStack) leftover.toArray()[0];
            int amountLeftover = leftoverStack.getAmount();
            Bukkit.getScheduler().runTaskAsynchronously(HolyCore.getInstance(), () -> {
                DatabaseManager db = DatabaseManager.getInstance();
                db.addCratesClaim(player.getUniqueId(), crateName, amountLeftover);
            });
            player.sendMessage(TextUtils.deserialize(Language.get("crates_overflow"), String.valueOf(amountLeftover)));
        }
    }
    public void claimKeys(Player player){
        UUID uuid = player.getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(HolyCore.getInstance(), () -> {
            DatabaseManager db = DatabaseManager.getInstance();
            Map<String, Integer> crates = db.getCratesClaims(uuid);
            int slotsNeeded = 0;
            // Find slots needed
            for(String crateKey : crates.keySet()){
                int crateStacks = crates.get(crateKey) / 64;
                slotsNeeded += crateStacks + (crates.get(crateKey) % 64 == 0 ? 0 : 1);
            }
            if(slotsNeeded == 0) {
                player.sendMessage(TextUtils.deserialize(Language.get("crate_no_claims")));
            }
            // Amount of empty player slots
            int emptySlots = (int) Arrays.stream(player.getInventory().getStorageContents())
                    .filter(Objects::isNull)
                    .count();
            if(emptySlots >= slotsNeeded) {
                // Give player crate keys
                for(String crateKey : crates.keySet()){
                    int crateStacks = crates.get(crateKey) / 64;
                    int crateRemainder = crates.get(crateKey) % 64;
                    if(crateStacks >= 1) {
                        for(int i = 0; i < crateStacks; i++){
                            giveKey(player, crateKey, 64);
                        }
                    }
                    if(crateRemainder > 0) {
                        giveKey(player, crateKey, crateRemainder);
                    }
                }
                // Clear claims from DB
                db.clearCratesClaims(uuid);
            } else {
                player.sendMessage(TextUtils.deserialize(Language.get("need_empty_slots"), String.valueOf(slotsNeeded)));
            }
        });
    }
    @Override
    public void execute(CommandSourceStack source, String[] args) {

        if(args.length == 0){
            source.getSender().sendMessage(TextUtils.deserialize(Language.get("crates_command_usage")));
            return;
        }
        if(args.length > 4) {
            source.getSender().sendMessage(TextUtils.deserialize(Language.get("crates_command_usage")));
            return;
        }
        String subcommand = args[0];
        if (subcommand == null) return;
        switch (subcommand.toLowerCase()) {
            case "set":
                if(!source.getSender().hasPermission("crates.admin")) return;
                if (!(source.getSender() instanceof Player player)){
                    source.getSender().sendMessage("This command can only be run by a player.");
                    return;
                }
                if (args.length == 2) {
                    Block targetBlock = player.getTargetBlockExact(4);
                    if (targetBlock != null) {
                        String crateName = args[1];
                        if (Crates.getCrates().containsKey(crateName)) {
                            player.sendMessage(TextUtils.deserialize(Language.get("crate_set"), crateName));
                            Crates.getCratesConfig().set(crateName + ".location", targetBlock.getLocation());
                            Crates.save();
                            Crates.reload();
                        } else {
                            player.sendMessage(TextUtils.deserialize(Language.get("crate_not_found")));
                        }
                    } else {
                        player.sendMessage(TextUtils.deserialize(Language.get("no_block_found")));
                    }

                }
                break;
            case "key":
                if(!source.getSender().hasPermission("crates.admin")) return;
                if(args.length >= 3) {
                    String crateName = args[1];
                    String playerName = args[2];
                    Player otherPlayer = Bukkit.getServer().getPlayerExact(playerName);
                    if(otherPlayer == null) {
                        source.getSender().sendMessage(TextUtils.deserialize(Language.get("player_not_found")));
                        return;
                    }
                    if(Crates.getCrates().containsKey(crateName)) {
                        ItemStack key = Crates.getCrates().get(crateName).getKeyItem();
                        if(args.length == 4) {
                            int amount;
                            try {
                                amount = Integer.parseInt(args[3]);
                                int maxStackSize = key.getMaxStackSize();
                                if(amount > maxStackSize) {
                                    source.getSender().sendMessage(TextUtils.deserialize(Language.get("max_stack_size"), Integer.toString(maxStackSize)));
                                    return;
                                }
                                giveKey(otherPlayer, crateName, amount);
                            } catch (NumberFormatException e) {
                                source.getSender().sendMessage(TextUtils.deserialize(Language.get("not_a_number")));
                                return;
                            }
                        } else {
                            giveKey(otherPlayer, crateName, 1);
                        }
                    } else {
                        source.getSender().sendMessage(TextUtils.deserialize(Language.get("crate_not_found")));
                    }
                }
                break;
            case "rewards":
                if (!(source.getSender() instanceof Player player)){
                    source.getSender().sendMessage("This command can only be run by a player.");
                    return;
                }
                if(args.length == 2) {
                    String crateName = args[1];
                    if(Crates.getCrates().containsKey(crateName)){
                        RewardsMenu menu = Crates.getCrates().get(crateName).menu();
                        player.openInventory(menu.getInventory());
                    }
                }
                break;
            case "claim":
                if (!(source.getSender() instanceof Player player)){
                    source.getSender().sendMessage("This command can only be run by a player.");
                    return;
                }
                claimKeys(player);
                break;
            case "reload":
                if(!source.getSender().hasPermission("crates.admin")) return;
                Crates.reload();
                source.getSender().sendMessage(TextUtils.deserialize(Language.get("config_reload")));
                break;
        }
    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        if(args.length <= 1 && source.getSender().hasPermission("crates.admin")) {
            return List.of("set", "key", "rewards", "reload", "claim");
        }
        if(args.length <= 1) {
            return List.of("rewards", "claim");
        }
        if(args.length == 2) {
            return Crates.getCrates().keySet();
        }
        if(args.length == 3 && source.getSender().hasPermission("crates.admin")) {
            // list of online players
            return Bukkit.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
