package org.beeholy.holyCore.model;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.beeholy.holyCore.HolyCore;
import org.beeholy.holyCore.gui.RewardsMenu;
import org.beeholy.holyCore.utility.Crates;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Crate {
    private String name;
    private List<Reward> rewards;
    private Location location;
    private String key;
    private String keyName;
    private List<String> keyLore;
    private String customModelData;

    public Crate(String key, String name, List<Reward> rewards, Location location, String keyName, List<String> keyLore, String customModelData) {
        this.key = key;
        this.rewards = rewards;
        this.name = name;
        this.location = location;
        this.keyName = keyName;
        this.keyLore = keyLore;
        this.customModelData = customModelData;
    }

    public String getName() {
        return name;
    }

    public ItemStack getKeyItem(){
        ItemStack item = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = item.getItemMeta();

        // Deserialize lore
        List<Component> deserializedLore = new ArrayList<>();
        for( String line : keyLore) {
            deserializedLore.add(TextUtils.deserialize(line).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }

        // set meta
        meta.displayName(TextUtils.deserialize(keyName).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        meta.lore(deserializedLore);

        CustomModelDataComponent modelDataComponent = meta.getCustomModelDataComponent();
        modelDataComponent.setStrings(List.of(customModelData));
        meta.setCustomModelDataComponent(modelDataComponent);

        NamespacedKey namespacedKey = new NamespacedKey(HolyCore.getInstance(), "crate_key");
        meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, key);

        item.setItemMeta(meta);
        item.setAmount(1);
        return item;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey(){
        return key;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public Location getLocation() {
        return location;
    }

    public RewardsMenu menu() {
        return new RewardsMenu(
                TextUtils.deserialize(this.getName()),
                27,
                this.getRewards()
        );
    }
}
