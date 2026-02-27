package org.beeholy.holyCore.skins;

import org.beeholy.holyCore.HolyCore;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SkinRegistry {

    private static Map<String, Skin> skins = new HashMap<>();

    public void registerAll(Map<String, Skin> loadedSkins) {
        skins.putAll(loadedSkins);
    }


    public Skin get(String id) {
        Skin skin = skins.get(id);
        return skin;
    }

    public Collection<String> getKeys(){
        return skins.keySet();
    }


    public Collection<Skin> getAll() {
        return skins.values();
    }
}
