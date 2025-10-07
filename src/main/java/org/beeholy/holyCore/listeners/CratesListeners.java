package org.beeholy.holyCore.listeners;

import org.beeholy.holyCore.HolyCore;
import org.beeholy.holyCore.model.Crate;
import org.beeholy.holyCore.model.Reward;
import org.beeholy.holyCore.utility.Crates;
import org.beeholy.holyCore.utility.Language;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class CratesListeners implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();

        Block block = event.getClickedBlock();
        if (block == null) return;
        Crate crate = Crates.fromLocation(block.getLocation());
        if (crate == null) return;

        if (event.getAction().isLeftClick()) {
            player.openInventory(crate.menu().getInventory());
            return;
        }

        ItemStack activeItem = player.getInventory().getItemInMainHand();
        ItemMeta itemMeta = activeItem.getItemMeta();

        if (itemMeta == null) return;
        NamespacedKey key = new NamespacedKey(HolyCore.getInstance(), "crate_key");
        if (!(itemMeta.getPersistentDataContainer().has(key))) return;

        if (crate.getKey().equals(itemMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING))) {
            // roll rewards / open crate gui
            // Check for space in inventory
            if (player.getInventory().firstEmpty() != -1) {
                Reward reward = Crates.rollReward(crate.getRewards());
                CommandSender console = Bukkit.getServer().getConsoleSender();
                String command = reward.getCommand().replace("<user>", player.getName());
                Bukkit.getServer().dispatchCommand(console, command);
                player.sendMessage(TextUtils.deserialize(Language.get("crate_won"), reward.getName()));
                int stackSize = activeItem.getAmount();
                activeItem.setAmount(stackSize - 1);
                event.setCancelled(true);
            } else {
                player.sendMessage(TextUtils.deserialize(Language.get("no_empty_slot")));
            }

        }
    }
}
