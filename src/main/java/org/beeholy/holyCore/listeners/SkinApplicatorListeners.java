package org.beeholy.holyCore.listeners;

import com.nexomc.nexo.api.NexoItems;
import org.beeholy.holyCore.HolyCore;
import org.beeholy.holyCore.items.ItemFactory;
import org.beeholy.holyCore.skins.Skin;
import org.beeholy.holyCore.skins.SkinManager;
import org.beeholy.holyCore.skins.SkinRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class SkinApplicatorListeners implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        if (e.isCancelled()) return;

        if (!(e.getClickedInventory() instanceof PlayerInventory)) return;

        ItemStack cursor = e.getCursor();
        ItemStack clicked = e.getCurrentItem();

        if (cursor == null || clicked == null) return;

        NamespacedKey applicatorKey = new NamespacedKey(HolyCore.getInstance(), "skin_applicator");

        if(!cursor.hasItemMeta()) return;
        //if(!clicked.hasItemMeta()) return;

        ItemMeta cursorMeta = cursor.getItemMeta();
        ItemMeta clickedMeta = clicked.getItemMeta();

        if(!cursorMeta.getPersistentDataContainer().has(applicatorKey)) return;

        String model = cursorMeta.getPersistentDataContainer().get(applicatorKey, PersistentDataType.STRING);
        String cursorModel = cursorMeta.getItemModel().toString();

        if(Objects.equals(model, "")){
            model = cursorModel;
        }

        SkinManager manager = HolyCore.getInstance().getSkinService().getSkinManager();
        SkinRegistry registry = manager.getRegistry();
        // If clicked has a model, if it's in our db, attempt to skin
        // if model is outside of db, do nothing
        if(clickedMeta == null) return;
        if(clickedMeta.hasItemModel()) {
            Skin skin = registry.get("nexo:" + NexoItems.idFromItem(clicked));
            Skin cursorSkin = manager.getSkinByModel(model);
            if(skin == cursorSkin) return;
            if(skin != null){
                // Item is in our db, apply new skin and return old in applicator
                if(manager.apply(cursorSkin.getId(), clicked)){
                    e.setCancelled(true);
                    ItemStack applicator = ItemFactory.createSkinApplicator(skin);
                    if(cursor.getAmount() == 1){
                        player.setItemOnCursor(applicator);
                    } else {
                        player.give(applicator);
                        cursor.setAmount(cursor.getAmount() - 1);
                    }

                }
            }
            // Item is not in our db, do nothing
            return;
        }
        // Decrement 1 from cursor and apply
        if(manager.apply(cursorModel, clicked)){
            e.setCancelled(true);
            cursor.setAmount(cursor.getAmount() - 1);
        }
    }
}
