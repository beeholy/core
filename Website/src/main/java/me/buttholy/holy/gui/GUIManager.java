package me.buttholy.holy.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class GUIManager {
    private final JavaPlugin plugin;

    public GUIManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    public void openGUI(Player player, GUI gui){
        player.openInventory(setupInventory(gui));
        plugin.getServer().getPluginManager().registerEvents(new GUIListeners(gui, this),this.plugin);
    }

    private Inventory setupInventory(GUI gui){
        String title = ChatColor.translateAlternateColorCodes('&', gui.getTitle());
        Inventory inventory = Bukkit.createInventory(null,gui.getSize(),gui.getTitle());

        for(Entry entry : gui.getEntries()){
            inventory.setItem(entry.getSlot(), buildEntry(entry));
        }

        gui.setInventory(inventory);

        return inventory;
    }

    private ItemStack buildEntry(Entry entry) {
        ItemStack item = new ItemStack(entry.getMaterial(), entry.getQuantity());
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(entry.getName());
        if(entry.getLore() != null && entry.getLore().size() > 0){
            ArrayList<String> lore = new ArrayList<>();

            for (String currentLine : entry.getLore()) {
                String line = ChatColor.translateAlternateColorCodes('&', currentLine);
                lore.add(line);
            }

            meta.setLore(lore);
        }

        item.setItemMeta(meta);

        return item;
    }

}
