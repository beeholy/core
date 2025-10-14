package org.beeholy.holyCore.gui;

import net.kyori.adventure.text.Component;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

import java.util.List;

public abstract class PaginatedMenu<T> extends Menu {

    protected final int pageSize;
    protected final int nextIndex;
    protected final int previousIndex;
    protected final int closeIndex;
    protected List<T> items;
    protected int page;

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

        // Blanks
        List<Integer> blanks = List.of(0, 1, 2, 3, 5, 6, 7, 8);
        for (int slot : blanks) {
            getInventory().setItem(pageSize + slot, createControlItem("blank", ""));
        }

        // Navigation Buttons
        if (items.size() > pageSize) {
            inv.setItem(previousIndex, createControlItem("left_arrow", "<italic:false><gray>Previous</gray>"));
            inv.setItem(nextIndex, createControlItem("right_arrow", "<italic:false><gray>Next</gray>"));
        }

        inv.setItem(closeIndex, createControlItem("close", "<italic:false><red>Close</red>"));
    }

    protected ItemStack createControlItem(String type, String name) {
        ItemStack item = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        CustomModelDataComponent component = meta.getCustomModelDataComponent();
        switch (type) {
            case "left_arrow":
                component.setStrings(List.of("ui_left_arrow"));
                break;
            case "right_arrow":
                component.setStrings(List.of("ui_right_arrow"));
                break;
            case "close":
                component.setStrings(List.of("ui_close"));
                break;
            case "blank":
                component.setStrings(List.of("ui_blank"));
                meta.setHideTooltip(true);
                break;
        }
        meta.setCustomModelDataComponent(component);
        meta.displayName(TextUtils.deserialize(name));
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

    public int getNextIndex() {
        return nextIndex;
    }

    public int getPreviousIndex() {
        return previousIndex;
    }

    public int getCloseIndex() {
        return closeIndex;
    }

}