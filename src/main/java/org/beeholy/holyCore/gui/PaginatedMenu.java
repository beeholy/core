package org.beeholy.holyCore.gui;

import net.kyori.adventure.text.Component;
import org.beeholy.holyCore.HolyCore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public abstract class PaginatedMenu<T> extends Menu {

    protected List<T> items;
    protected int page;
    protected final int pageSize;
    protected final int nextIndex;
    protected final int previousIndex;
    protected final int closeIndex;

    @Override
    public void handleClick(InventoryClickEvent event) {
        super.handleClick(event);
        int slot = event.getRawSlot();
        if (slot == getNextIndex()) {
            nextPage();
            event.setCancelled(true);
        } else if (slot == getPreviousIndex()) {
            previousPage();
            event.setCancelled(true);
        } else if (slot == getCloseIndex()) {
            event.getWhoClicked().closeInventory();
            event.setCancelled(true);
        }
    }

    public PaginatedMenu(Component title, int size, List<T> items) {
        super(title, size);
        this.items = items;
        this.pageSize = size - 9;
        this.page = 0;
        this.nextIndex = pageSize + 5;
        this.previousIndex = pageSize + 3;
        this.closeIndex = pageSize + 4;
        updateInventory();
    }
    /**
     * Implement this method to generate the display item for a specific data entry
     */
    protected abstract ItemStack buildItem(T data);

    public void setItems(List<T> items) {
        this.items = items;
    }

    /**
     * Call this whenever the page or content needs to be refreshed
     */
    protected void updateInventory() {
        Inventory inv = getInventory();
        inv.clear();

        int start = page * pageSize;
        int end = Math.min(start + pageSize, items.size());

        for (int i = start; i < end; i++) {
            T data = items.get(i);
            inv.setItem(i - start, buildItem(data));
        }

        // Navigation Buttons
        if (items.size() > pageSize) {
            inv.setItem(previousIndex, createControlItem(Material.ARROW, ChatColor.YELLOW + "Previous Page"));
            inv.setItem(nextIndex, createControlItem(Material.ARROW, ChatColor.YELLOW + "Next Page"));
        }

        inv.setItem(closeIndex, createControlItem(Material.BARRIER, ChatColor.RED + "Close Menu"));
    }

    protected ItemStack createControlItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    public void nextPage() {
        if ((page + 1) * pageSize < items.size()) {
            page++;
            updateInventory();
        }
    }

    public void previousPage() {
        if (page > 0) {
            page--;
            updateInventory();
        }
    }

    public int getNextIndex() { return nextIndex; }
    public int getPreviousIndex() { return previousIndex; }
    public int getCloseIndex() { return closeIndex; }

}