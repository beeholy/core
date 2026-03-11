package org.beeholy.holyCore.listeners.enchants;


import com.destroystokyo.paper.MaterialSetTag;
import io.papermc.paper.entity.PlayerGiveResult;
import io.papermc.paper.event.block.BlockBreakProgressUpdateEvent;
import io.papermc.paper.event.block.PlayerShearBlockEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static org.bukkit.Material.*;

public class BreakToolListeners implements Listener {

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

    Enchantment treeFeller = Registry.ENCHANTMENT.get(
            NamespacedKey.fromString("holymc:treefeller")
    );

    Enchantment harvester = Registry.ENCHANTMENT.get(
            NamespacedKey.fromString("holymc:harvester")
    );

    Enchantment replant = Registry.ENCHANTMENT.get(
            NamespacedKey.fromString("holymc:replant")
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

    private Material getReplantable(Material material) {
        return switch (material) {
            case WHEAT -> WHEAT_SEEDS;
            case POTATOES -> POTATO;
            case CARROTS -> CARROT;
            case BEETROOTS -> BEETROOT_SEEDS;
            case NETHER_WART -> NETHER_WART;
            default -> null;
        };
    }

    private boolean isLogBlock(Material material){
        return MaterialSetTag.LOGS.isTagged(material);
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

    private HashSet<Block> getLogBlocks(Location loc, int amount) {
        HashSet<Block> blocks = new HashSet<>(Set.of(loc.getBlock()));
        HashSet<Block> newestBlocks = new HashSet<>(Set.of(loc.getBlock()));

        int depth = 0;

        while (depth < amount) {
            HashSet<Block> tempBlocks = new HashSet<>();

            for (Block block1 : newestBlocks) {
                for (Block block : getSurroundingBlocks(block1.getLocation())) {
                    if (!blocks.contains(block) && isLogBlock(block.getType())) tempBlocks.add(block);
                }
            }

            blocks.addAll(tempBlocks);
            newestBlocks = tempBlocks;

            ++depth;
        }

        return blocks;
    }

    private HashSet<Block> getSurroundingBlocks(Location loc) {
        HashSet<Block> blocks = new HashSet<>();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {

                    // Skip the center block itself
                    if (x == 0 && y == 0 && z == 0) continue;

                    blocks.add(loc.clone().add(x, y, z).getBlock());
                }
            }
        }

        return blocks;
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
    private final Map<UUID, Location> lastLocationBroken = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            Player player = e.getPlayer();
            lastBrokenFace.put(player.getUniqueId(), e.getBlockFace());
            lastMaterial.put(player.getUniqueId(), e.getClickedBlock().getType());
            lastLocationBroken.put(player.getUniqueId(), e.getClickedBlock().getLocation());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.isCancelled()) return;
        Player player = e.getPlayer();

        ItemStack usedItem = player.getInventory().getItemInMainHand();
        Location location = e.getBlock().getLocation();

        if(location.equals(lastLocationBroken.get(player.getUniqueId()))){

            BlockFace blockFace = lastBrokenFace.get(player.getUniqueId());

            // Three Break
            if(usedItem.getEnchantments().containsKey(threeBreak)){
                if(player.isSneaking()) return;
                HashSet<Block> blocks = getBlocks(location, blockFace, usedItem.getEnchantmentLevel(threeBreak) - 1);

                for(Block block : blocks) {
                    if(!block.getLocation().equals(lastLocationBroken.get(player.getUniqueId()))) {
                        if (block.getType() == lastMaterial.get(player.getUniqueId())) {
                            player.breakBlock(block);
                        }
                    }
                }
            }
            // Harvester
            if(usedItem.getEnchantments().containsKey(harvester)){
                HashSet<Block> blocks = getBlocks(location, BlockFace.DOWN, 0);

                for(Block block : blocks) {
                    if(!block.getLocation().equals(lastLocationBroken.get(player.getUniqueId()))) {
                        if (block.getType() == lastMaterial.get(player.getUniqueId())) {
                            if(block.getBlockData() instanceof Ageable data) {
                                if (data.getAge() == data.getMaximumAge()){
                                    player.breakBlock(block);
                                }
                            }

                        }
                    }
                }
            }
            // Vein Miner
            if(usedItem.getEnchantments().containsKey(veinMiner)){

                if(isOreBlock(lastMaterial.get(player.getUniqueId()))) {
                    HashSet<Block> blocks = getOreBlocks(location, usedItem.getEnchantmentLevel(veinMiner));
                    int maxBlocks = 0;
                    for(Block block : blocks) {
                        if(!block.getLocation().equals(lastLocationBroken.get(player.getUniqueId()))) {
                            if (maxBlocks > 32) return;
                            if (block.getType() == lastMaterial.get(player.getUniqueId())) {
                                player.breakBlock(block);
                            }
                        }
                        maxBlocks++;
                    }
                }
            }

            if(usedItem.getEnchantments().containsKey(treeFeller)){

                if(isLogBlock(lastMaterial.get(player.getUniqueId()))) {
                    HashSet<Block> blocks = getLogBlocks(location, usedItem.getEnchantmentLevel(treeFeller));

                    int maxBlocks = 0;
                    for(Block block : blocks) {
                        if(!block.getLocation().equals(lastLocationBroken.get(player.getUniqueId()))) {
                            if (maxBlocks > 64) return;
                            if (block.getType() == lastMaterial.get(player.getUniqueId())) {
                                player.breakBlock(block);
                            }
                        }
                        maxBlocks++;
                    }
                }
            }
        }

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

        Block block = e.getBlock();

        ItemStack usedItem = player.getInventory().getItemInMainHand();

        // Drops Awarded at end - after processing with telek check
        List<ItemStack> itemDrops = e.getItems()
                .stream()
                .map(Item::getItemStack)
                .collect(Collectors.toCollection(ArrayList::new));

        Location location = e.getBlock().getLocation();

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
//        if(usedItem.getEnchantments().containsKey(telekinesis)){
//            giveOrDrop(player, location, itemDrops);
//        } else {
//            Item item = e.getItems().get(0);
//            dropItems(location, itemDrops);
//        }

        if(usedItem.getEnchantments().containsKey(telekinesis)){
            List<ItemStack> returns = giveReturnDrops(player, itemDrops);
            itemDrops.clear();
            itemDrops.addAll(returns);
        }

        if(usedItem.getEnchantments().containsKey(replant)){

            Material farmableType = e.getBlockState().getType();
            Material replantMaterial = getReplantable(farmableType);

            if(replantMaterial != null) {
                if(player.getInventory().first(replantMaterial) != -1) {
                    ItemStack replantItem = player.getInventory().getItem(player.getInventory().first(replantMaterial));
                    if (replantItem != null) {
                        replantItem.setAmount(replantItem.getAmount() - 1);
                        block.setType(farmableType);
                    }
                }
            }
        }

        if (!itemDrops.isEmpty()) {
            List<Item> items = e.getItems();

            for (int i = 0; i < items.size(); i++) {
                if (i < itemDrops.size()) {
                    items.get(i).setItemStack(itemDrops.get(i));
                } else {
                    items.get(i).remove();
                }
            }

            // If you need more items than originally dropped:
            if (itemDrops.size() > items.size()) {
                Location loc = e.getBlock().getLocation();
                for (int i = items.size(); i < itemDrops.size(); i++) {
                    items.add(loc.getWorld().dropItemNaturally(loc, itemDrops.get(i)));
                }
            }
        } else {
            for (Item item : e.getItems()) {
                item.remove();
            }
        }

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

            e.getDrops().clear();
            e.getDrops().addAll(giveReturnDrops(player, items));
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
