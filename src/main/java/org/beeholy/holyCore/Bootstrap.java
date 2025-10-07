package org.beeholy.holyCore;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.tag.PostFlattenTagRegistrar;
import io.papermc.paper.tag.TagEntry;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Tag;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Bootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {

        // Create new key for tools
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.TAGS.preFlatten(RegistryKey.ITEM), event -> {
            TagKey<ItemType> toolsTag = TagKey.create(RegistryKey.ITEM, "holymc:tools");
            event.registrar().setTag(toolsTag, List.of(
                    TagEntry.tagEntry(ItemTypeTagKeys.PICKAXES),
                    TagEntry.tagEntry(ItemTypeTagKeys.SHOVELS),
                    TagEntry.tagEntry(ItemTypeTagKeys.AXES),
                    TagEntry.tagEntry(ItemTypeTagKeys.HOES)
            ));
        });
        // Telekinesis registration
        TypedKey<Enchantment> Telekinesis = EnchantmentKeys.create(Key.key("holymc:telekinesis"));
        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.compose().newHandler(event -> {
            event.registry().register(
                    Telekinesis,
                    b -> b.description(Component.text("Telekinesis"))
                            .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.create(Key.key("holymc:tools"))))
                            .anvilCost(1)
                            .maxLevel(1)
                            .weight(10)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                            .activeSlots(EquipmentSlotGroup.ANY)
            );
        }));

        context.getLifecycleManager().registerEventHandler(LifecycleEvents.TAGS.postFlatten(RegistryKey.ENCHANTMENT), event -> {
            final PostFlattenTagRegistrar<Enchantment> registrar = event.registrar();
            // add to enchanting table and villager trades
            //registrar.addToTag(EnchantmentTagKeys.IN_ENCHANTING_TABLE, Set.of(Telekinesis));
            //registrar.addToTag(EnchantmentTagKeys.TRADEABLE,Set.of(Telekinesis));
        });
    }
}