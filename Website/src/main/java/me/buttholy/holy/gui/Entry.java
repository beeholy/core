package me.buttholy.holy.gui;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

abstract public class Entry {

    public Entry(Material material, String name, List<String> lore, int quantity, int slot) {
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.quantity = quantity;
        this.slot = slot;
    }

    private Material material;
    private String name;
    private List<String> lore;
    private int quantity;
    private int slot;

    public Material getMaterial(){
        return this.material;
    }

    public String getName(){
        return this.name;
    }

    public List<String> getLore(){
        return this.lore;
    }

    public int getQuantity(){
        return this.quantity;
    }

    public int getSlot(){
        return this.slot;
    }

    public abstract void onClick(InventoryClickEvent event);
    public abstract void onChange();
}
