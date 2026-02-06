package me.buttholy.holy.gui;

import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;

abstract public class GUI {
    public GUI(String identifier, int size, String title) {
        this.identifier = identifier;
        this.size = size;
        this.title = title;
    }

    private String identifier;

    private int size;
    private String title;

    private Inventory inventory;

    private List<Entry> entries;

    public String getIdentifier(){
        return this.identifier;
    }

    public int getSize(){
        return this.size;
    }

    public String getTitle(){
        return this.title;
    }

    public Inventory getInventory(){
        return this.inventory;
    }

    public void setInventory(Inventory inventory){
        this.inventory = inventory;
    }

    public List<Entry> getEntries(){
        return this.entries;
    }
    public abstract void onOpen(InventoryOpenEvent event);
    public abstract void onClose(InventoryCloseEvent event);
}
