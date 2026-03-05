package org.beeholy.holyCore.skins;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SkinRegistry {

    private static final Map<String, Skin> skins = new HashMap<>();

    public void registerAll(Map<String, Skin> loadedSkins) {
        skins.putAll(loadedSkins);
    }


    public Skin get(String id) {
        return skins.get(id);
    }

    public Collection<String> getKeys(){
        return skins.keySet();
    }


    public Collection<Skin> getAll() {
        return skins.values();
    }
}
