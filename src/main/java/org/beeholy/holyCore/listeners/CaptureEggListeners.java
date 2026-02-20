package org.beeholy.holyCore.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.chat.ComponentSerializer;
import org.beeholy.holyCore.HolyCore;
import org.beeholy.holyCore.hooks.GPHook;
import org.beeholy.holyCore.items.ItemFactory;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaptureEggListeners implements Listener {
    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        ItemStack mainhand = player.getInventory().getItemInMainHand();
        if (!(mainhand.getItemMeta() instanceof SpawnEggMeta)) return;
        SpawnEggMeta meta = (SpawnEggMeta) mainhand.getItemMeta();
        if(meta == null) return;
        NamespacedKey key = new NamespacedKey(HolyCore.getInstance(), "capture_egg_empty");

        Boolean empty = meta.getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN);
        if (empty == null) return;
        event.setCancelled(true);

        if (!empty) return;
        Entity entity = event.getRightClicked();

        if (!(entity instanceof Mob)) return;

        Location entityLocation = entity.getLocation();

        if(!(GPHook.hasBuildPermission(player, entityLocation))) {
            return;
        }

        EntitySnapshot snapshot = entity.createSnapshot();
        meta.setSpawnedEntity(snapshot);

        ArrayList<String> loreLines = new ArrayList<>();
        ArrayList<Component> lore = new ArrayList<>();

        loreLines.addAll(List.of(
           "",
           "<italic:false><gold>| <white>Right click to place mob",
           ""
        ));
        for(String line: loreLines){
            lore.add(TextUtils.deserialize(line));
        }

        if(entity instanceof Villager villager){
            List<MerchantRecipe> recipes = villager.getRecipes();
            for(MerchantRecipe recipe : recipes) {
                List<ItemStack> ingredients = recipe.getIngredients();
                ItemStack result = recipe.getResult();
                if(!(result.hasItemMeta())) continue;

                Map<Enchantment,Integer> enchants = result.getItemMeta().getEnchants();

                if(result.getItemMeta() instanceof EnchantmentStorageMeta enchantMeta){
                    enchants = enchantMeta.getStoredEnchants();
                }

                if(enchants.isEmpty())
                    continue;

                Component ingredientsComponent =
                        Component.empty()
                                .append(result.effectiveName().color(TextColor.color(255, 170, 0)))
                                .append(Component.text(" for ")).color(TextColor.color(255, 255, 255))
                                .append(Component.text(ingredients.getFirst().getAmount() + " "))
                                .append(ingredients.getFirst().effectiveName().color(TextColor.color(255, 170, 0)))
                                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
                lore.add(ingredientsComponent);

                for(Map.Entry<Enchantment,Integer> entry : enchants.entrySet()) {
                    Component enchantsComponent = Component.empty()
                            .append(TextUtils.deserialize("<italic:false><gold> - </gold>"))
                            .append(entry.getKey().displayName(entry.getValue()))
                            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
                    lore.add(enchantsComponent);
                }
            }
        }

        // If mob doesnt have an egg, don't allow
        Material material = Bukkit.getItemFactory().getSpawnEgg(entity.getType());
        if (material == null) return;

        ItemStack newItem = new ItemStack(material);
        entity.remove();
        meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, false);
        TextReplacementConfig config = TextReplacementConfig
                .builder()
                .match("(?:ʀᴇᴜѕᴀʙʟᴇ|ѕɪɴɢʟᴇ ᴜѕᴇ)").replacement(TextUtils.toSmallText(entity.getType().name())).build();

        meta.displayName(meta.displayName().replaceText(config))
        ;
        meta.lore(lore);
        newItem.setItemMeta(meta);

        if(mainhand.getAmount() == 1) {
            player.getInventory().setItemInMainHand(newItem);
        } else {
            mainhand.setAmount(mainhand.getAmount() - 1);
            player.give(newItem);
        }
    }
    @EventHandler
    public void onPlace(PlayerInteractEvent e) {
        if (e.isCancelled()) return;
        Player p = e.getPlayer();
        ItemStack mainhand = p.getInventory().getItemInMainHand();
        if (!(mainhand.getItemMeta() instanceof SpawnEggMeta)) return;
        SpawnEggMeta meta = (SpawnEggMeta) mainhand.getItemMeta();
        if(meta == null) return;
        NamespacedKey key = new NamespacedKey(HolyCore.getInstance(), "capture_egg_empty");

        // Check for key (is it Egg)
        Boolean empty = meta.getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN);
        if (empty == null) return;
        if (empty) {
            e.setCancelled(true);
            return;
        };
        // It is spawn egg, our behaviour
        e.setCancelled(true);
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {

            meta.getSpawnedEntity();
            //Location spawnLoc = e.getClickedBlock().getLocation().add(0,1,0);
            Location spawnLoc = e.getInteractionPoint();
            Location locPlus1 = spawnLoc.clone().add(0, 1, 0);
            if(locPlus1.getBlock().isSuffocating()) return;

            // Spawn and decrement the amount

            meta.getSpawnedEntity().createEntity(spawnLoc);
            mainhand.setAmount(mainhand.getAmount() - 1);

            NamespacedKey reusableKey = new NamespacedKey(HolyCore.getInstance(), "capture_egg_reusable");
            Boolean isReusable = meta.getPersistentDataContainer().get(reusableKey, PersistentDataType.BOOLEAN);
            if(isReusable == null) return;
            if(isReusable) {
                ItemStack newItem = ItemFactory.createMobCaptureEgg(true);
                if (mainhand.getAmount() == 0) {
                    p.getInventory().setItemInMainHand(newItem);
                } else {
                    p.give(newItem);
                }
            }
        }
    }
}
