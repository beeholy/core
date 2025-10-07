package org.beeholy.holyCore.items;

import net.kyori.adventure.text.Component;
import org.beeholy.holyCore.HolyCore;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class ItemFactory {
    public static ItemStack createSpawnerPick() {
        ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE); // You can choose any item
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(TextUtils.deserialize("<italic:false><grey>Spawner Pickaxe</grey></italic>")); // Optional: custom name
            NamespacedKey key = new NamespacedKey(HolyCore.getInstance(), "spawner_pick");
            meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, Boolean.TRUE);
            meta.lore(List.of(
                    Component.empty(),
                    TextUtils.deserialize("<italic:false><gold>| </gold><white>Use this to mine spawners</white>"),
                    TextUtils.deserialize(""),
                    TextUtils.deserialize("<italic:false><red>| </red><white>One time use</white>")
            ));
            CustomModelDataComponent customModel = meta.getCustomModelDataComponent();
            customModel.setStrings(List.of("spawner_pick"));
            meta.setCustomModelDataComponent(customModel);
            item.setItemMeta(meta);
        }
        return item;
    }
}
