package org.beeholy.holyCore.model;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class Reward {
    private final String name;
    private final List<String> lore;
    private final double chance;
    private final String command;
    private final ItemStack item;

    public Reward(String name, List<String> lore, double chance, String command, String material, String customModelData, int stackSize) {
        this.name = name;
        this.lore = lore;
        this.chance = chance;
        this.command = command;
        this.item = new ItemStack(Material.valueOf(material));
        ItemMeta meta = this.item.getItemMeta();
        meta.displayName(TextUtils.deserialize(name).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        List<Component> deserializedLore = new ArrayList<>();
        for( String line : lore) {
            deserializedLore.add(TextUtils.deserialize(line).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }
        meta.lore(deserializedLore);

        CustomModelDataComponent modelDataComponent = meta.getCustomModelDataComponent();
        modelDataComponent.setStrings(List.of(customModelData));
        meta.setCustomModelDataComponent(modelDataComponent);

        this.item.setItemMeta(meta);
        this.item.setAmount(stackSize);
    }

    // Getters

    public ItemStack getItemStack() {
        return item;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public double getChance() {
        return chance;
    }

    public String getCommand() {
        return command;
    }
}