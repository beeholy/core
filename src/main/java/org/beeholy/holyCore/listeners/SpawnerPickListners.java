package org.beeholy.holyCore.listeners;

import org.beeholy.holyCore.HolyCore;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class SpawnerPickListners implements Listener {
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.SPAWNER) return;

        ItemStack mainHand = event.getPlayer().getInventory().getItemInMainHand();
        ItemStack offHand = event.getPlayer().getInventory().getItemInOffHand();
        NamespacedKey pickKey = new NamespacedKey(HolyCore.getInstance(), "spawner_pick");

        boolean hasPickMain = mainHand != null && mainHand.getItemMeta() != null &&
                mainHand.getItemMeta().getPersistentDataContainer().has(pickKey, PersistentDataType.BYTE);

        if (!hasPickMain) return;

        CreatureSpawner spawner = (CreatureSpawner) block.getState();
        EntityType type = spawner.getSpawnedType();

        // Create spawner item with entity type stored
        ItemStack spawnerItem = new ItemStack(Material.SPAWNER);
        ItemMeta meta = spawnerItem.getItemMeta();
        if (meta != null) {
            NamespacedKey typeKey = new NamespacedKey(HolyCore.getInstance(), "spawner_type");
            meta.getPersistentDataContainer().set(typeKey, PersistentDataType.STRING, type.name());
            meta.displayName(TextUtils.deserialize("<italic:false><gold>" + TextUtils.prettifyEnum(type) + " Spawner</gold>"));
            spawnerItem.setItemMeta(meta);
        }

        event.setExpToDrop(0);

        // Remove the pickaxe used
        event.getPlayer().getInventory().setItemInMainHand(spawnerItem);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (block.getType() != Material.SPAWNER) return;

        ItemStack item = event.getItemInHand();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        NamespacedKey typeKey = new NamespacedKey(HolyCore.getInstance(), "spawner_type");
        String typeName = meta.getPersistentDataContainer().get(typeKey, PersistentDataType.STRING);
        if (typeName == null) return;

        EntityType type = EntityType.valueOf(typeName);
        CreatureSpawner spawner = (CreatureSpawner) block.getState();
        spawner.setSpawnedType(type);
        spawner.update();
    }
}
