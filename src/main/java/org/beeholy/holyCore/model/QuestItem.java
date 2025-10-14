package org.beeholy.holyCore.model;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class QuestItem extends ItemStack {
    public List<String> getClickCommands() {
        return clickCommands;
    }

    List<String> clickCommands;
    public QuestItem(Player player, String type, int amount, String name, List<String> lore, String custom_model, List<String> click, Boolean hideTooltip) {
        super(Material.valueOf(type), amount);
        clickCommands = click;
        ItemMeta meta = this.getItemMeta();

        meta.displayName(TextUtils.deserialize(PlaceholderAPI.setPlaceholders(player, name)));

        ArrayList<Component> loreComponents = new ArrayList<>();
        for(String line : lore){
            loreComponents.add(TextUtils.deserialize(PlaceholderAPI.setPlaceholders(player, line)));
        }
        meta.lore(loreComponents);
        CustomModelDataComponent model = meta.getCustomModelDataComponent();
        model.setStrings(List.of(custom_model));
        meta.setCustomModelDataComponent(model);
        meta.setHideTooltip(hideTooltip);

        this.setItemMeta(meta);
    }

    public void onLeftClick(){

    }
}
