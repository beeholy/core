package org.beeholy.holyCore.gui;

import net.kyori.adventure.text.Component;
import org.beeholy.holyCore.HolyCore;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class Menu implements InventoryHolder {

    private final Inventory inventory;

    public Menu(Component title, int size) {
        // Create an Inventory with 9 slots, `this` here is our InventoryHolder.
        this.inventory = HolyCore.getInstance().getServer().createInventory(this, size, title);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void handleClick(InventoryClickEvent event) {
        // Default: do nothing
    }
}
