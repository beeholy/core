package org.beeholy.holyCore.skins;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SkinApplier {

    public static boolean applySkin(ItemStack item, Skin skin) {
        if (item == null || !skin.canApplyTo(item.getType())) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        String[] skinId = skin.getId().split(":");
        ItemBuilder itemBuilder = NexoItems.itemFromId(skinId[1]);

        if(itemBuilder==null) return false;

        ItemStack customItem = itemBuilder.build();
        ItemMeta customMeta = customItem.getItemMeta();

        meta.getEnchants().forEach((enchantment, integer) -> {
            customMeta.addEnchant(enchantment, integer, true);
        });
        customMeta.lore(meta.lore());

        meta = customMeta;

        //meta.setItemModel(NamespacedKey.fromString(skin.getId()));

        Component displayName = item.displayName();
        String newName = PlainTextComponentSerializer.plainText().serialize(displayName);
        newName = newName.substring(1, newName.length() - 1);

        meta.displayName(
                MiniMessage.miniMessage().deserialize(
                        skin.getGradient() + newName
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
