package org.beeholy.holyCore.skins;

import org.bukkit.Material;

import java.util.Set;

    public class Skin {

        private final String id;
        private final String name;
        private final String gradient;
        private final String model;
        private final Set<Material> appliesTo;
        public String getId() {
            return id;
        }

        public String getModel() {
            return model;
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
                Set<Material> appliesTo,
                String model
        ) {
            this.id = id;
            this.name = name;
            this.gradient = gradient;
            this.model = model;
            this.appliesTo = appliesTo;
        }


        public boolean canApplyTo(Material material) {
            return appliesTo.contains(material);
        }



    }
