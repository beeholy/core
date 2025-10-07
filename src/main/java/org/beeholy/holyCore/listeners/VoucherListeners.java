package org.beeholy.holyCore.listeners;

import org.beeholy.holyCore.HolyCore;
import org.beeholy.holyCore.items.Vouchers;
import org.beeholy.holyCore.utility.Language;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VoucherListeners implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction().isLeftClick()) return;
        Player player = event.getPlayer();
        ItemStack activeItem = player.getInventory().getItemInMainHand();
        ItemMeta itemMeta = activeItem.getItemMeta();

        if(itemMeta == null) return;

        NamespacedKey key = new NamespacedKey(HolyCore.getInstance(), "voucher_name");
        if(!(itemMeta.getPersistentDataContainer().has(key))) return;

        event.setCancelled(true);
        String voucherName = itemMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);

        if(Vouchers.getPermission(voucherName) != null) {
            if (player.isPermissionSet(Vouchers.getPermission(voucherName))) {
                player.sendMessage(TextUtils.deserialize(Language.get("perms_already_has")));
                return;
            }
        }
        if(Vouchers.getSuccessMessage(voucherName) != null){
            player.sendMessage(TextUtils.deserialize(Vouchers.getSuccessMessage(voucherName)));
        }
        CommandSender console = Bukkit.getServer().getConsoleSender();
        String command = Vouchers.getCommand(voucherName).replace("<user>", player.getName());

        // Regex pattern to match <min-max> like <100-500>
        Pattern rangePattern = Pattern.compile("<(\\d+)-(\\d+)>");
        Matcher matcher = rangePattern.matcher(command);

        StringBuffer replacedCommand = new StringBuffer();
        while (matcher.find()) {
            int min = Integer.parseInt(matcher.group(1));
            int max = Integer.parseInt(matcher.group(2));
            int random = ThreadLocalRandom.current().nextInt(min, max + 1); // inclusive upper bound
            matcher.appendReplacement(replacedCommand, String.valueOf(random));
        }
        matcher.appendTail(replacedCommand);

        Bukkit.getServer().dispatchCommand(console, replacedCommand.toString());
        HolyCore.getInstance().getLogger().info("command: " +  replacedCommand.toString());
        int stackSize = activeItem.getAmount();
        activeItem.setAmount(stackSize - 1);
    }
}