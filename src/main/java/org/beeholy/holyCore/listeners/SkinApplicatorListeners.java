package org.beeholy.holyCore.listeners;

import org.beeholy.holyCore.HolyCore;
import org.beeholy.holyCore.items.ItemFactory;
import org.beeholy.holyCore.skins.Skin;
import org.beeholy.holyCore.skins.SkinManager;
import org.beeholy.holyCore.skins.SkinRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class SkinApplicatorListeners implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        if (e.isCancelled()) return;

        ItemStack cursor = e.getCursor();
        ItemStack clicked = e.getCurrentItem();

        if (cursor == null || clicked == null) return;

        NamespacedKey applicatorKey = new NamespacedKey(HolyCore.getInstance(), "skin_applicator");

        if(!cursor.hasItemMeta()) return;
        //if(!clicked.hasItemMeta()) return;

        ItemMeta cursorMeta = cursor.getItemMeta();
        ItemMeta clickedMeta = clicked.getItemMeta();

        if(!cursorMeta.getPersistentDataContainer().has(applicatorKey)) return;

        String customModel = cursorMeta.getItemModel().toString();

        SkinManager manager = HolyCore.getInstance().getSkinService().getSkinManager();
        SkinRegistry registry = manager.getRegistry();
        // If clicked has a model, if it's in our db, attempt to skin
        // if model is outside of db, do nothing
        if(clickedMeta == null) return;
        if(clickedMeta.hasItemModel()) {
            Skin skin = registry.get(clickedMeta.getItemModel().toString());
            if(skin == registry.get(customModel)) return;
            if(skin != null){
                // Item is in our db, apply new skin and return old in applicator
                if(manager.apply(customModel, clicked)){
                    e.setCancelled(true);
                    ItemFactory.createSkinApplicator(skin);
                    player.give(ItemFactory.createSkinApplicator(skin));
                    cursor.setAmount(cursor.getAmount() - 1);
                }
            }
            // Item is not in our db, do nothing
            return;
        }
        // Decrement 1 from cursor and apply
        if(manager.apply(customModel, clicked)){
            e.setCancelled(true);
            cursor.setAmount(cursor.getAmount() - 1);
        }
    }
}
