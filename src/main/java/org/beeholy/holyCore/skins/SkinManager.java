package org.beeholy.holyCore.skins;

import org.bukkit.inventory.ItemStack;

import java.util.Objects;

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

    public Skin getSkinByModel(String model){
        for(Skin skin : getRegistry().getAll()) {

            if(Objects.equals(skin.getId(), model)) return skin;

            if(Objects.equals(skin.getModel(), model)){return skin;}

        }
        return null;
    }
    public SkinRegistry getRegistry() {
        return registry;
    }
}
