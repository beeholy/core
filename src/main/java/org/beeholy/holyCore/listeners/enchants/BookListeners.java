package org.beeholy.holyCore.listeners.enchants;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class BookListeners implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        ItemStack cursor = e.getCursor();
        ItemStack clicked = e.getCurrentItem();

        if (cursor == null || clicked == null) return;
        if (!clicked.hasItemMeta()) return;

        // =====================================================
        // 📕 BOOK → EXTRACT RANDOM ENCHANT FROM ITEM
        // =====================================================
        if (cursor.getType() == Material.BOOK) {

            Map<Enchantment, Integer> enchants =
                    clicked.getItemMeta().getEnchants();

            if (enchants.isEmpty()) return;

            e.setCancelled(true);

            // Pick random enchant
            List<Map.Entry<Enchantment, Integer>> entries =
                    new ArrayList<>(enchants.entrySet());

            Map.Entry<Enchantment, Integer> chosen =
                    entries.get(ThreadLocalRandom.current().nextInt(entries.size()));

            Enchantment enchantment = chosen.getKey();
            int level = chosen.getValue();

            // Remove enchant safely
            if (clicked.getType() == Material.ENCHANTED_BOOK) {
                EnchantmentStorageMeta meta =
                        (EnchantmentStorageMeta) clicked.getItemMeta();
                meta.removeStoredEnchant(enchantment);
                clicked.setItemMeta(meta);
            } else {
                clicked.removeEnchantment(enchantment);
            }

            // Create enchanted book
            ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta bookMeta =
                    (EnchantmentStorageMeta) enchantedBook.getItemMeta();

            bookMeta.addStoredEnchant(enchantment, level, true);
            enchantedBook.setItemMeta(bookMeta);

            // Consume book & give result
            cursor.setAmount(cursor.getAmount() - 1);
            player.give(cursor);
            player.setItemOnCursor(enchantedBook);
            return;
        }

        // =====================================================
        // 📘 ENCHANTED BOOK → APPLY ONE RANDOM ENCHANT (SAFE)
        // =====================================================
        if (cursor.getType() == Material.ENCHANTED_BOOK) {

            if (!(cursor.getItemMeta() instanceof EnchantmentStorageMeta bookMeta))
                return;

            Map<Enchantment, Integer> stored = bookMeta.getStoredEnchants();
            if (stored.isEmpty()) return;

            e.setCancelled(true);

            ItemMeta itemMeta = clicked.getItemMeta();

            // Pick ONE random stored enchant
            List<Map.Entry<Enchantment, Integer>> storedEntries =
                    new ArrayList<>(stored.entrySet());

            Map.Entry<Enchantment, Integer> chosen =
                    storedEntries.get(ThreadLocalRandom.current().nextInt(storedEntries.size()));

            Enchantment enchantment = chosen.getKey();
            int level = chosen.getValue();

            // Vanilla safety checks
            if (!enchantment.canEnchantItem(clicked)) return;

            boolean conflicts = itemMeta.getEnchants().keySet()
                    .stream()
                    .anyMatch(existing -> existing.conflictsWith(enchantment));

            if (conflicts) return;

            // Apply enchant safely
            itemMeta.addEnchant(enchantment, level, false);
            clicked.setItemMeta(itemMeta);

            // Consume enchanted book
            cursor.setAmount(cursor.getAmount() - 1);
            if (cursor.getAmount() <= 0) {
                player.setItemOnCursor(null);
            }
        }
    }
}
