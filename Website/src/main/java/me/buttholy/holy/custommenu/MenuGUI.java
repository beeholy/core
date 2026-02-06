package me.buttholy.holy.custommenu;

import me.buttholy.holy.custommenu.entries.HealEntry;
import me.buttholy.holy.gui.Entry;
import me.buttholy.holy.gui.GUI;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.Arrays;
import java.util.List;

public class MenuGUI extends GUI {
    private List<Entry> entries;

    public MenuGUI(String identifier, int size, String title) {
        super(identifier, size, title);
        this.entries = Arrays.asList(
                new HealEntry(Material.GOLDEN_APPLE, "&6Heal",Arrays.asList("This","Is Lore"), 1, 13)
        );
    }


    @Override
    public List<Entry> getEntries(){
        return this.entries;
    }
    @Override
    public void onOpen(InventoryOpenEvent event) {

    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }
}
