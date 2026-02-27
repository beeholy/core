package org.beeholy.holyCore.skins;

import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SkinApplier {

    public static boolean applySkin(ItemStack item, Skin skin) {
        if (item == null || !skin.canApplyTo(item.getType())) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        meta.setItemModel(NamespacedKey.fromString(skin.getId()));

        meta.displayName(
                MiniMessage.miniMessage().deserialize(
                        skin.getGradient() + skin.getName()
                ).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
        );

        item.setItemMeta(meta);
        return true;
    }

    public static void removeSkin(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        meta.setItemModel(null);
        item.setItemMeta(meta);
    }
}
