package org.beeholy.holyCore.listeners;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Duration;

public class DurabilityListener implements Listener {
    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable damageable)) return;

        int maxDurability = item.getType().getMaxDurability();
        if (maxDurability <= 0) return;

        int currentDamage = damageable.getDamage();
        int newDamage = currentDamage + event.getDamage();
        int remaining = maxDurability - newDamage;

        if (remaining <= 10) {
            final Title.Times times = Title.Times.times(Duration.ofMillis(100), Duration.ofMillis(1500), Duration.ofMillis(100));
            final Component mainTitle = Component.text("Durability Warning").color(NamedTextColor.RED);
            final Component subtitle = Component.text("Your tool is about to break!").color(NamedTextColor.RED);

            // Creates a simple title with the default values for fade-in, stay on screen and fade-out durations
            final Title title = Title.title(mainTitle,subtitle, times);

            // Send the title to your audience
            event.getPlayer().showTitle(title);
        }
    }
}
