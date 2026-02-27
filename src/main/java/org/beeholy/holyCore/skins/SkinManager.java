package org.beeholy.holyCore.skins;

import org.beeholy.holyCore.utility.Language;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SkinManager {

    private final SkinRegistry registry;

    public SkinManager(SkinRegistry registry) {
        this.registry = registry;
    }

    public boolean apply(String skinId, ItemStack item) {
        Skin skin = registry.get(skinId);
        if(skin == null) return false;
        return SkinApplier.applySkin(item, skin);
    }

    public void remove(ItemStack item) {
        SkinApplier.removeSkin(item);
    }

    public SkinRegistry getRegistry() {
        return registry;
    }
}
