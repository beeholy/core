package me.buttholy.holy.custommenu.entries;

import me.buttholy.holy.gui.Entry;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class HealEntry extends Entry {
    public HealEntry(Material material, String name, List<String> lore, int quantity, int slot) {
        super(material, name, lore, quantity, slot);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getView().getPlayer();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.sendMessage(ChatColor.GREEN + "You have been healed.");
    }

    @Override
    public void onChange() {

    }
}
