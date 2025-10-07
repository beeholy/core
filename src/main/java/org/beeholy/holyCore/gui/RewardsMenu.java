package org.beeholy.holyCore.gui;

import net.kyori.adventure.text.Component;
import org.beeholy.holyCore.model.Reward;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RewardsMenu extends PaginatedMenu<Reward> {

    public RewardsMenu(Component title, int size, List<Reward> rewards) {
        super(title, size, rewards);
    }

    @Override
    protected ItemStack buildItem(Reward data) {
        return data.getItemStack();
    }

    @Override
    public void handleClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        super.handleClick(event);
        event.setCancelled(true);
    }
}
