package me.buttholy.holy.customitem;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;

import java.util.Arrays;

public class CaptureEgg extends ItemStack {
    public CaptureEgg() {
        super(Material.MOOSHROOM_SPAWN_EGG);
        SpawnEggMeta eggMeta = (SpawnEggMeta) this.getItemMeta();
        assert eggMeta != null;
        eggMeta.setDisplayName("Capture Egg (Single Use)");
        eggMeta.setLore(Arrays.asList("Right click to capture mob"));
        this.setItemMeta(eggMeta);
    }
}