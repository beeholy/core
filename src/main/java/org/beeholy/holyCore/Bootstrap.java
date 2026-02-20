package org.beeholy.holyCore;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.keys.ItemTypeKeys;
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.tag.PostFlattenTagRegistrar;
import io.papermc.paper.tag.TagEntry;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;

import java.util.List;

public class Bootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {

        // Create new key for tools
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.TAGS.preFlatten(RegistryKey.ITEM), event -> {
            TagKey<ItemType> toolsTag = TagKey.create(RegistryKey.ITEM, "holymc:tools");
            TagKey<ItemType> shearsTag = TagKey.create(RegistryKey.ITEM, "holymc:shears");

            event.registrar().setTag(toolsTag, List.of(
                    TagEntry.tagEntry(ItemTypeTagKeys.PICKAXES),
                    TagEntry.tagEntry(ItemTypeTagKeys.SHOVELS),
                    TagEntry.tagEntry(ItemTypeTagKeys.AXES),
                    TagEntry.tagEntry(ItemTypeTagKeys.HOES),
                    TagEntry.valueEntry(
                            TypedKey.create(
                                    RegistryKey.ITEM,
                                    ItemType.SHEARS.key()
                            )
                    )
            ));

            event.registrar().setTag(shearsTag, List.of(
                    TagEntry.valueEntry(
                            TypedKey.create(
                                    RegistryKey.ITEM,
                                    ItemType.SHEARS.key()
                            )
                    )
            ));
        });


        // Telekinesis registration
        TypedKey<Enchantment> Telekinesis = EnchantmentKeys.create(Key.key("holymc:telekinesis"));
        // 3x3 Break
        TypedKey<Enchantment> ThreeBreak = EnchantmentKeys.create(Key.key("holymc:threebreak"));

        TypedKey<Enchantment> VeinMiner = EnchantmentKeys.create(Key.key("holymc:veinminer"));

        TypedKey<Enchantment> AutoSmelt = EnchantmentKeys.create(Key.key("holymc:autosmelt"));

        TypedKey<Enchantment> GlassCutter = EnchantmentKeys.create(Key.key("holymc:glasscutter"));


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
                            .activeSlots(EquipmentSlotGroup.MAINHAND)
            );

            event.registry().register(
                    ThreeBreak,
                    b -> b.description(Component.text("3 x 3 Break"))
                            .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.create(Key.key("holymc:tools"))))
                            .anvilCost(30)
                            .maxLevel(3)
                            .weight(10)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 10))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 10))
                            .activeSlots(EquipmentSlotGroup.MAINHAND)
                            .exclusiveWith(RegistrySet.keySet(
                                    RegistryKey.ENCHANTMENT,
                                    List.of(
                                            VeinMiner,
                                            GlassCutter
                                    )
                            ))
            );

            event.registry().register(
                    VeinMiner,
                    b -> b.description(Component.text("Vein Miner"))
                            .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.PICKAXES))
                            .anvilCost(10)
                            .maxLevel(10)
                            .weight(10)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 10))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 10))
                            .activeSlots(EquipmentSlotGroup.MAINHAND)
                            .exclusiveWith(RegistrySet.keySet(
                                    RegistryKey.ENCHANTMENT,
                                    List.of(
                                            ThreeBreak
                                    )
                            ))
            );

            event.registry().register(
                    AutoSmelt,
                    b -> b.description(Component.text("Auto Smelt"))
                            .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.PICKAXES))
                            .anvilCost(40)
                            .maxLevel(4)
                            .weight(10)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 10))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 10))
                            .activeSlots(EquipmentSlotGroup.MAINHAND)
                            .exclusiveWith(RegistrySet.keySet(
                                RegistryKey.ENCHANTMENT,
                                List.of(
                                    EnchantmentKeys.SILK_TOUCH
                                )
                            ))
            );

            event.registry().register(
                    GlassCutter,
                    b -> b.description(Component.text("Glass Cutter"))
                            .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.create(Key.key("holymc:shears"))))
                            .anvilCost(40)
                            .maxLevel(3)
                            .weight(10)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 10))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 10))
                            .activeSlots(EquipmentSlotGroup.MAINHAND)
            );

        }));

        // Modify Silk touch (for Shears)
        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.entryAdd()
                // Increase the max level to 20
                .newHandler(event -> event.builder()
                        .supportedItems(
                                event.getOrCreateTag(
                                        ItemTypeTagKeys.create(Key.key("holymc:tools"))
                                )
                        )
                )
                // Configure the handler to only be called for the Vanilla sharpness enchantment.
                .filter(EnchantmentKeys.SILK_TOUCH)
        );


    }
}