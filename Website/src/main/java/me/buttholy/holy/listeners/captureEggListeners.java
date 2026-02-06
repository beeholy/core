package me.buttholy.holy.listeners;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class captureEggListeners implements Listener {

    // Handle capturing mob
    @EventHandler
    public void onRightClickEntity(PlayerInteractEntityEvent event) {
        ItemStack heldItemStack = event.getPlayer().getInventory().getItemInMainHand();
        ItemMeta heldItemMeta = heldItemStack.getItemMeta();
        if (heldItemMeta instanceof SpawnEggMeta) {
            // if empty capture mob
            if (heldItemMeta.getDisplayName().equals("Capture Egg (Single Use)") && heldItemMeta.hasLore()) {
                event.getPlayer().sendMessage("BH: spawn egg meta");
                switch(event.getRightClicked().getType().toString()) {
                    case "CREEPER":
                        heldItemMeta.setLore(List.of("Creeper"));
                        heldItemStack.setItemMeta(heldItemMeta);
                        event.getRightClicked().remove();
                        break;
                    case "PIG":
                        heldItemMeta.setLore(List.of("Pig"));
                        heldItemStack.setItemMeta(heldItemMeta);
                        event.getRightClicked().remove();
                        break;
                }
                List<MetadataValue> values = event.getRightClicked().getMetadata("attributes");
                for(int i = 0; i < values.size(); i++) {
                    event.getPlayer().sendMessage(values.get(i).asString());
                }
            }
            event.setCancelled(true);
        }
    }
    // Handle placing mob
    @EventHandler
    public void onRightClickBlock(PlayerInteractEvent event){
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack heldItemStack = event.getPlayer().getInventory().getItemInMainHand();
            ItemMeta heldItemMeta = heldItemStack.getItemMeta();
            if (heldItemMeta instanceof SpawnEggMeta) {
                if (heldItemMeta.getDisplayName().equals("Capture Egg (Single Use)") &&
                        heldItemMeta.hasLore())
                {
                    // if the egg isnt empty
                    if(!heldItemMeta.getLore().get(0).equals("Right click to capture mob")) {
                        //place mob
                    }
                    event.getPlayer().sendMessage("Spawn egg place");
                    //event.setCancelled(true);
                    event.setUseItemInHand(Event.Result.DENY);
                }
            }
        }
    }
    // Handle renaming capture egg in anvil (cancel)
}
