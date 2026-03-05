package org.beeholy.holyCore.skins;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SkinConfigLoader {

    public static Map<String, Skin> load(ConfigurationSection section) {
        Map<String, Skin> skins = new HashMap<>();

        if (section == null) return skins;

        for (String id : section.getKeys(false)) {
            ConfigurationSection skinSec = section.getConfigurationSection(id);

            String name = skinSec.getString("name");
            String gradient = skinSec.getString("gradient");
            String model = skinSec.getString("model");

            Set<Material> appliesTo = skinSec.getStringList("applies_to")
                    .stream()
                    .map(String::toUpperCase)
                    .map(Material::valueOf)
                    .collect(Collectors.toSet());

            if (model != null){
                skins.put(id, new Skin(
                        id,
                        name,
                        gradient,
                        appliesTo,
                        model
                ));
            } else {
                skins.put(id, new Skin(
                        id,
                        name,
                        gradient,
                        appliesTo,
                        ""
                ));
            }


        }

        return skins;
    }
}
