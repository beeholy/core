package org.beeholy.holyCore.hooks;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class GPHook {
    public static boolean isWorldEnabled(UUID uuid) {
        World world = Bukkit.getWorld(uuid);
        return GriefPrevention.instance.claimsEnabledForWorld(world);
    }

    public static boolean hasBuildPermission(Player player, Location location){
        List<Claim> claimList = getClaims().stream().toList();
        Claim cacheClaim = claimList.get(0);
        Claim locationHasClaim = GriefPrevention.instance.dataStore.getClaimAt(location, true, cacheClaim);
        if(locationHasClaim == null) return true;

        if(player.hasPermission("mobegg.bypass")) return true;

        return locationHasClaim.hasExplicitPermission(player, ClaimPermission.Build);
    }

    public static Collection<Claim> getClaims() {
        return GriefPrevention.instance.dataStore.getClaims();
    }
}
