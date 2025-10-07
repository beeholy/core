package org.beeholy.holyCore.listeners;

import io.papermc.paper.event.player.PlayerNameEntityEvent;
import org.beeholy.holyCore.HolyCore;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class OverrideListeners implements Listener {
    private final NamespacedKey voucherKey = new NamespacedKey(HolyCore.getInstance(), "voucher_name");
    private final NamespacedKey crateKey = new NamespacedKey(HolyCore.getInstance(), "crate_key");

    @EventHandler
    public void onMobRename(PlayerNameEntityEvent event) {
        Player player = event.getPlayer();
        ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
        if (meta == null) return;
        boolean hasVoucher = meta.getPersistentDataContainer().has(voucherKey);
        boolean hasCrateKey = meta.getPersistentDataContainer().has(crateKey);
        event.setCancelled(hasVoucher || hasCrateKey);
    }

    @EventHandler
    public void onAnvilRename(PrepareAnvilEvent event) {
        // First item as second slot doesn't allow renaming
        ItemStack stack1 = event.getInventory().getFirstItem();

        if (stack1 != null) {
            ItemMeta meta1 = stack1.getItemMeta();
            if (meta1 == null) return;

            if (meta1.getPersistentDataContainer().has(voucherKey)
                    || meta1.getPersistentDataContainer().has(crateKey)) {
                event.setResult(null);
            }
        }
    }
}
