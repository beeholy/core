package org.beeholy.holyCore.hooks;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;

import java.util.Map;
import java.util.stream.Collectors;

public class LPHook {

    static LuckPerms luckPerms = LuckPermsProvider.get();


    public static LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public static Map<String, Integer> getRankPriority() {
        return luckPerms.getGroupManager().getLoadedGroups().stream()
                .collect(Collectors.toMap(
                        Group::getName,
                        group -> group.getWeight().orElse(0) // fallback to 0 if no weight
                ));
    }
}
