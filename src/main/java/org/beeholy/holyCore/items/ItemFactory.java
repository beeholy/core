package org.beeholy.holyCore.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.beeholy.holyCore.HolyCore;
import org.beeholy.holyCore.skins.Skin;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ItemFactory {
    public static ItemStack createSkinApplicator(Skin skin){
        ItemStack itemStack = ItemStack.of(Material.PAPER);
        ItemMeta meta = itemStack.getItemMeta();

        NamespacedKey applicatorKey = new NamespacedKey(HolyCore.getInstance(), "skin_applicator");

        if(skin.getModel() != "") {
            meta.getPersistentDataContainer().set(applicatorKey, PersistentDataType.STRING, skin.getModel());
            meta.setItemModel(NamespacedKey.fromString(skin.getModel()));
        } else {
            meta.getPersistentDataContainer().set(applicatorKey, PersistentDataType.STRING, "");
            meta.setItemModel(NamespacedKey.fromString(skin.getId()));
        }

        meta.displayName(
                MiniMessage.miniMessage().deserialize(
                        skin.getGradient() + skin.getName()
                ).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                        .append(Component.text(" Applicator").color(NamedTextColor.WHITE))
        );

        ArrayList<Component> lore = new ArrayList<>(List.of(Component.empty(),
                TextUtils.deserialize("<italic:false><gray>| <white>Drag and drop onto "),
                TextUtils.deserialize("<italic:false><gray>| <white>your item to apply "),
                Component.empty(),
                TextUtils.deserialize("<italic:false><gray>Applies to: </gray>")));

        for(Material material : skin.getAppliesTo()){
            lore.add(TextUtils.deserialize("<italic:false><gray>| </gray><white>" + TextUtils.prettifyEnum(material) + "</white>"));
        }
        meta.lore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack createSpawnerPick() {
        ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE); // You can choose any item
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(TextUtils.deserialize("<italic:false><grey>ѕᴘᴀᴡɴᴇʀ ᴘɪᴄᴋᴀxᴇ</grey></italic>")); // Optional: custom name
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

    public static ItemStack createMobCaptureEgg(boolean reusable){
        ItemStack item = new ItemStack(Material.GHAST_SPAWN_EGG);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            // Set PDC
            PersistentDataContainer container = meta.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(HolyCore.getInstance(), "capture_egg_empty");
            container.set(key, PersistentDataType.BOOLEAN, true);

            ArrayList<Component> lore = new ArrayList<>();
            ArrayList<String> loreLines = new ArrayList<>();

            if(reusable){
                NamespacedKey reusableKey = new NamespacedKey(HolyCore.getInstance(), "capture_egg_reusable");
                container.set(reusableKey, PersistentDataType.BOOLEAN, true);
                meta.displayName(TextUtils.deserialize("<italic:false><white>Mob Capture Egg <gold>[ʀᴇᴜѕᴀʙʟᴇ]"));

            } else {
                meta.displayName(TextUtils.deserialize("<italic:false><white>Mob Capture Egg <gray>[ѕɪɴɢʟᴇ ᴜѕᴇ]"));
            }
            loreLines.addAll(List.of(
                    "",
                    "<italic:false><gold>| <white>Right click to capture a mob ",
                    ""
            ));
            // Deserialize each lore then set
            for(String s : loreLines){
                lore.add(TextUtils.deserialize(s));
            }
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
