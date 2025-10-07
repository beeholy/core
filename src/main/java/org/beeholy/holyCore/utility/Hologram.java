package org.beeholy.holyCore.utility;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;

public class Hologram {

    public static Hologram instance;
    public static HashMap<String, Hologram> holograms = new HashMap<>();

    public Hologram(){
        instance = this;
    }


    public void create(World world, Location location){

    }

    public void delete(){}
    public void update(){}
}
