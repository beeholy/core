package me.buttholy.holy.gui;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;

public class GUIListeners implements Listener {
    GUI gui;
    GUIManager manager;

    public GUIListeners(GUI gui, GUIManager manager){
        this.gui = gui;
        this.manager = manager;
    }
    @EventHandler
    public void onClick(InventoryClickEvent event){
        // cancel click if top inv is our gui
        if(event.getWhoClicked().getOpenInventory().getTopInventory().equals(gui.getInventory())){
            event.setCancelled(true);
        }
        // if the click was in our gui (instead of in inv) pass to click handler
        if(event.getClickedInventory().equals(gui.getInventory())){

            int clickedSlot = event.getSlot();

            for(Entry entry: gui.getEntries()){
                if(entry.getSlot() == clickedSlot) {
                    entry.onClick(event);
                    break;
                }
            }
        };
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        if (event.getInventory().equals(gui.getInventory())) {
            gui.onOpen(event);
        }
    }
    @EventHandler
    public void onItemMove(InventoryInteractEvent event) {
        if(event.getInventory().equals(gui.getInventory())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onItemDrag(InventoryDragEvent event) {
        if(event.getInventory().equals(gui.getInventory())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onClose(InventoryCloseEvent event){
        if(event.getInventory().equals(gui.getInventory())){
            gui.onClose(event);
            InventoryCloseEvent.getHandlerList().unregister(this);
            InventoryOpenEvent.getHandlerList().unregister(this);
            InventoryClickEvent.getHandlerList().unregister(this);
            InventoryDragEvent.getHandlerList().unregister(this);
        }
    }
}
