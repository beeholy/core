package org.beeholy.holyCore.listeners.enchants;

import io.papermc.paper.entity.PlayerGiveResult;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WeaponListeners implements Listener {

    Enchantment telekinesis = Registry.ENCHANTMENT.get(
            NamespacedKey.fromString("holymc:telekinesis")
    );

    private List<ItemStack> giveReturnDrops(Player player, List<ItemStack> items){
        List<ItemStack> returns = new ArrayList<>();
        for(ItemStack i : items) {
            PlayerGiveResult result = player.give(Collections.singleton(i), false);
            if(!result.leftovers().isEmpty()){
                returns.add(i);
            }
        }
        return returns;
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent event){
        if(!(event.getDamageSource().getCausingEntity() instanceof Player player)) return;

        if (!player.getInventory().getItemInMainHand().hasItemMeta()) return;

        ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();

        if(meta.hasEnchant(telekinesis)){
            List<ItemStack> returnedDrops = giveReturnDrops(player, event.getDrops());
            event.getDrops().clear();
            event.getDrops().addAll(returnedDrops);
        }

    }
}
