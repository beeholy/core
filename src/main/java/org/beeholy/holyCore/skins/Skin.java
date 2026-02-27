package org.beeholy.holyCore.skins;

import org.beeholy.holyCore.HolyCore;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Set;

    public class Skin {

        private final String id;
        private final String name;
        private final String gradient;
        private final Set<Material> appliesTo;
        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getGradient() {
            return gradient;
        }

        public Set<Material> getAppliesTo() {
            return appliesTo;
        }


        public Skin(
                String id,
                String name,
                String gradient,
                Set<Material> appliesTo
        ) {
            this.id = id;
            this.name = name;
            this.gradient = gradient;
            this.appliesTo = appliesTo;
        }


        public boolean canApplyTo(Material material) {
            return appliesTo.contains(material);
        }



    }
