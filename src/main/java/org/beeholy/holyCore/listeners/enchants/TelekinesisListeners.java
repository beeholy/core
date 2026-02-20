package org.beeholy.holyCore.listeners.enchants;


import io.papermc.paper.entity.PlayerGiveResult;
import io.papermc.paper.event.block.BlockBreakProgressUpdateEvent;
import io.papermc.paper.event.block.PlayerShearBlockEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class TelekinesisListeners implements Listener {

    Enchantment telekinesis = Registry.ENCHANTMENT.get(
            NamespacedKey.fromString("holymc:telekinesis")
    );
    Enchantment threeBreak = Registry.ENCHANTMENT.get(
            NamespacedKey.fromString("holymc:threebreak")
    );

    Enchantment veinMiner = Registry.ENCHANTMENT.get(
            NamespacedKey.fromString("holymc:veinminer")
    );

    Enchantment autoSmelt = Registry.ENCHANTMENT.get(
            NamespacedKey.fromString("holymc:autosmelt")
    );

    Enchantment glassCutter = Registry.ENCHANTMENT.get(
            NamespacedKey.fromString("holymc:glasscutter")
    );

    private void giveOrDrop(Player player, Location location, List<ItemStack> items){
        for(ItemStack i : items) {
            PlayerGiveResult result = player.give(Collections.singleton(i), false);
            if(!result.leftovers().isEmpty()){
                World world = location.getWorld();
                world.dropItem(location, i);
            }
        }
    }

    private void dropItems(Location location, List<ItemStack> items){
        for(ItemStack i : items) {
            World world = location.getWorld();
            world.dropItemNaturally(location, i);
        }
    }


    private boolean isOreBlock(Material material) {
        return switch (material) {
            case COAL_ORE, DEEPSLATE_COAL_ORE,
                 COPPER_ORE, DEEPSLATE_COPPER_ORE,
                 DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE,
                 EMERALD_ORE, DEEPSLATE_EMERALD_ORE,
                 GOLD_ORE, DEEPSLATE_GOLD_ORE,
                 IRON_ORE, DEEPSLATE_IRON_ORE,
                 LAPIS_ORE, DEEPSLATE_LAPIS_ORE,
                 REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE,
                 NETHER_GOLD_ORE,
                 NETHER_QUARTZ_ORE -> true;
            default -> false;
        };
    }

    private HashSet<Block> getOreBlocks(Location loc, int amount) {
        HashSet<Block> blocks = new HashSet<>(Set.of(loc.getBlock()));
        HashSet<Block> newestBlocks = new HashSet<>(Set.of(loc.getBlock()));

        int depth = 0;

        while (depth < amount) {
            HashSet<Block> tempBlocks = new HashSet<>();

            for (Block block1 : newestBlocks) {
                for (Block block : getSurroundingBlocks(block1.getLocation())) {
                    if (!blocks.contains(block) && isOreBlock(block.getType())) tempBlocks.add(block);
                }
            }

            blocks.addAll(tempBlocks);
            newestBlocks = tempBlocks;

            ++depth;
        }

        return blocks;
    }

    private HashSet<Block> getSurroundingBlocks(Location loc) {
        HashSet<Block> locations = new HashSet<>();

        locations.add(loc.clone().add(0,1,0).getBlock());
        locations.add(loc.clone().add(0,-1,0).getBlock());
        locations.add(loc.clone().add(1,0,0).getBlock());
        locations.add(loc.clone().add(-1,0,0).getBlock());
        locations.add(loc.clone().add(0,0,1).getBlock());
        locations.add(loc.clone().add(0,0,-1).getBlock());

        return locations;
    }

    private HashSet<Block> getBlocks(Location loc, BlockFace blockFace, Integer depth) {
        Location loc2 = loc.clone();

        switch (blockFace) {
            case SOUTH -> {
                loc.add(-1, 1, -depth);
                loc2.add(1, -1, 0);
            }

            case WEST -> {
                loc.add(depth, 1, -1);
                loc2.add(0, -1, 1);
            }

            case EAST -> {
                loc.add(-depth, 1, 1);
                loc2.add(0, -1, -1);
            }

            case NORTH -> {
                loc.add(1, 1, depth);
                loc2.add(-1, -1, 0);
            }

            case UP -> {
                loc.add(-1, -depth, -1);
                loc2.add(1, 0, 1);
            }

            case DOWN -> {
                loc.add(1, depth, 1);
                loc2.add(-1, 0, -1);
            }

            default -> {}


        }
        return getEnchantBlocks(loc, loc2);
    }

    public HashSet<Block> getEnchantBlocks(@NotNull Location loc, @NotNull Location loc2) {
        HashSet<Block> blockList = new HashSet<>();
        int topBlockX = (Math.max(loc.getBlockX(), loc2.getBlockX()));
        int bottomBlockX = (Math.min(loc.getBlockX(), loc2.getBlockX()));
        int topBlockY = (Math.max(loc.getBlockY(), loc2.getBlockY()));
        int bottomBlockY = (Math.min(loc.getBlockY(), loc2.getBlockY()));
        int topBlockZ = (Math.max(loc.getBlockZ(), loc2.getBlockZ()));
        int bottomBlockZ = (Math.min(loc.getBlockZ(), loc2.getBlockZ()));

        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                for (int y = bottomBlockY; y <= topBlockY; y++) {
                    if (loc.getWorld() != null) blockList.add(loc.getWorld().getBlockAt(x, y, z));
                }
            }
        }

        return blockList;
    }
    private final Map<UUID, BlockFace> lastBrokenFace = new HashMap<>();
    private final Map<UUID, Material> lastMaterial = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            Player player = e.getPlayer();
            lastBrokenFace.put(player.getUniqueId(), e.getBlockFace());
            lastMaterial.put(player.getUniqueId(), e.getClickedBlock().getType());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.isCancelled()) return;
    }

    @EventHandler
    public void onBlockBreakProgress(BlockBreakProgressUpdateEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;

        ItemStack tool = player.getInventory().getItemInMainHand();

        if (!tool.getEnchantments().containsKey(glassCutter)) return;

        if (!e.getBlock().getType().toString().toLowerCase().contains("glass")) return;

        int level = tool.getEnchantmentLevel(glassCutter);
        float progress = e.getProgress();

        // Level 4: break immediately
        if (level >= 3) {
            if(progress >= 0.1) {
                player.breakBlock(e.getBlock());
                return;
            }
        }

        float requiredProgress = 1.0f - (level * 0.2f);

        if (progress >= requiredProgress) {
            // Unsafe if we have recursive enchants
            player.breakBlock(e.getBlock());
        }
    }

    @EventHandler
    public void onBlockBreakDrop(BlockDropItemEvent e) {
        Player player = e.getPlayer();

        ItemStack usedItem = player.getInventory().getItemInMainHand();


        // Drops Awarded at end - after processing with telek check
        ArrayList<ItemStack> itemDrops = e.getItems()
                .stream()
                .map(Item::getItemStack)
                .collect(Collectors.toCollection(ArrayList::new));

        BlockFace blockFace = lastBrokenFace.get(player.getUniqueId());

        Location location = e.getBlock().getLocation();


        if(usedItem.getEnchantments().containsKey(threeBreak)){

            HashSet<Block> blocks = getBlocks(location, blockFace, usedItem.getEnchantmentLevel(threeBreak) - 1);

            for(Block block : blocks) {
                if (block.getType() == lastMaterial.get(player.getUniqueId())) {

                    itemDrops.addAll(block.getDrops(usedItem));

                    block.setType(Material.AIR);

                    if(usedItem.getItemMeta() instanceof Damageable damageable){
                        damageable.setDamage(damageable.getDamage() - 1);
                    }
                }
            }
        }
        // Vein Miner

        if(usedItem.getEnchantments().containsKey(veinMiner)){

            Bukkit.getLogger().info(lastMaterial.get(player.getUniqueId()).toString());
            if(isOreBlock(lastMaterial.get(player.getUniqueId()))) {
                HashSet<Block> blocks = getOreBlocks(location, usedItem.getEnchantmentLevel(veinMiner));

                for (Block block : blocks) {

                    itemDrops.addAll(block.getDrops(usedItem));
                    block.setType(Material.AIR);

                    if (usedItem.getItemMeta() instanceof Damageable damageable) {
                        damageable.setDamage(damageable.getDamage() - 1);
                    }
                }
            }
        }


        if(usedItem.getEnchantments().containsKey(autoSmelt)) {

            int level = usedItem.getEnchantmentLevel(autoSmelt);

            double chance = level * 0.25; // 1=0.25, 4=1.0

            List<ItemStack> newDrops = new ArrayList<>();
            Random random = new Random();

            for (ItemStack drop : itemDrops) {

                boolean smelt = random.nextDouble() <= chance;

                if (!smelt) {
                    newDrops.add(drop);
                    continue;
                }

                switch (drop.getType()) {
                    case RAW_IRON ->
                            newDrops.add(new ItemStack(Material.IRON_INGOT, drop.getAmount()));

                    case RAW_GOLD ->
                            newDrops.add(new ItemStack(Material.GOLD_INGOT, drop.getAmount()));

                    case RAW_COPPER ->
                            newDrops.add(new ItemStack(Material.COPPER_INGOT, drop.getAmount()));

                    default ->
                            newDrops.add(drop);
                }
            }
            itemDrops.clear();
            itemDrops.addAll(newDrops);
        }


        // Telekinesis Handling (handle Last) - put in inv or drop
        if(usedItem.getEnchantments().containsKey(telekinesis)){
            giveOrDrop(player, e.getBlock().getLocation(), itemDrops);
        } else {
            dropItems(e.getBlock().getLocation(), itemDrops);
        }
        e.getItems().clear();
    }

    // Shear Event (for beehive)
    @EventHandler
    public void onBlockShear(PlayerShearBlockEvent e) {
        if(e.isCancelled()) return;

        Player player = e.getPlayer();

        ItemStack item = e.getItem();
        // Check for Telekinesis Enchant

        if(item.getEnchantments().containsKey(telekinesis)){

            List<ItemStack> items = e.getDrops();

            giveOrDrop(player, e.getBlock().getLocation(), items);

            e.getDrops().clear();
        }
    }

    // Shear Event (for sheep)
    @EventHandler
    public void onEntityShear(PlayerShearEntityEvent e){
        if(e.isCancelled()) return;

        Player player = e.getPlayer();

        ItemStack item = e.getItem();

        // Check for Telekinesis Enchant

        if(item.getEnchantments().containsKey(telekinesis)){
            List<ItemStack> items = e.getDrops();
            giveOrDrop(player, e.getEntity().getLocation(), items);
            e.setDrops(List.of());
        }




    }
}
