package org.beeholy.holyCore.listeners.enchants;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class StatusEffectListeners implements Listener {

    Enchantment glowing = Registry.ENCHANTMENT.get(
            NamespacedKey.fromString("holymc:glowing")
    );
    Enchantment aquatic = Registry.ENCHANTMENT.get(
            NamespacedKey.fromString("holymc:aquatic")
    );
    Enchantment speed = Registry.ENCHANTMENT.get(
            NamespacedKey.fromString("holymc:speed")
    );
    Enchantment springs = Registry.ENCHANTMENT.get(
            NamespacedKey.fromString("holymc:springs")
    );

    Enchantment reach = Registry.ENCHANTMENT.get(
            NamespacedKey.fromString("holymc:reach")
    );

    Enchantment overload = Registry.ENCHANTMENT.get(
            NamespacedKey.fromString("holymc:overload")
    );

    @EventHandler
    public void onHandChange(PlayerItemHeldEvent event){

    }
    public void applyEnchants(Player player, ItemMeta meta){

        if (meta.hasEnchant(glowing)) {
            PotionEffect effect = new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 0, false, false);
            player.addPotionEffect(effect);
        }

        if (meta.hasEnchant(aquatic)) {
            PotionEffect effect = new PotionEffect(PotionEffectType.WATER_BREATHING, PotionEffect.INFINITE_DURATION, 0, false, false);
            player.addPotionEffect(effect);
        }
        if (meta.hasEnchant(speed)) {

            int level = meta.getEnchantLevel(speed);

            PotionEffect effect = new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, level - 1, false, false);

            player.addPotionEffect(effect);
        }

        if (meta.hasEnchant(overload)) {
            int level = meta.getEnchantLevel(overload);
            PotionEffect effect = new PotionEffect(PotionEffectType.HEALTH_BOOST, PotionEffect.INFINITE_DURATION, level - 1, false, false);
            player.addPotionEffect(effect);
        }

        if (meta.hasEnchant(springs)) {
            int level = meta.getEnchantLevel(springs);
            PotionEffect effect = new PotionEffect(PotionEffectType.JUMP_BOOST, PotionEffect.INFINITE_DURATION, level - 1, false, false);
            player.addPotionEffect(effect);
        }

        if (meta.hasEnchant(reach)) {
            int level = meta.getEnchantLevel(reach);
            player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).setBaseValue(4.5 + level);
            player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).setBaseValue(3 + level);
        }
    }

    public void removeEnchants(Player player, ItemMeta meta){
        if (meta.hasEnchant(glowing)) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
        if (meta.hasEnchant(aquatic)) {
            player.removePotionEffect(PotionEffectType.WATER_BREATHING);
        }

        if(meta.hasEnchant(speed)){
            player.removePotionEffect(PotionEffectType.SPEED);
        }

        if(meta.hasEnchant(springs)){
            player.removePotionEffect(PotionEffectType.JUMP_BOOST);
        }

        if(meta.hasEnchant(overload)){
            player.removePotionEffect(PotionEffectType.HEALTH_BOOST);
        }

        if (meta.hasEnchant(reach)) {
            player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).setBaseValue(4.5);
            player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).setBaseValue(3);
        }

    }

    @EventHandler
    public void onRespawn(PlayerPostRespawnEvent event){
        event.getPlayer().getInventory().getArmorContents();
    }

    @EventHandler
    public void onEquipChange(EntityEquipmentChangedEvent event){

        if(!(event.getEntity() instanceof Player player)) return;

        event.getEquipmentChanges().forEach( (slot, change) -> {
            if(slot.isArmor() ||
                    (slot.isHand() && slot == EquipmentSlot.HAND)) {
                // remove old effects
                if(change.oldItem().hasItemMeta()) {
                    ItemMeta oldMeta = change.oldItem().getItemMeta();
                    removeEnchants(player, oldMeta);
                }
                // Add new effects
                if(change.newItem().hasItemMeta()) {
                    ItemMeta newMeta = change.newItem().getItemMeta();
                    applyEnchants(player, newMeta);
                }
            }
        });

    }
}
