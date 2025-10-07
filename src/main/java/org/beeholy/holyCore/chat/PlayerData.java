package org.beeholy.holyCore.chat;

import org.beeholy.holyCore.HolyCore;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class PlayerData {
    public static void setString(Player player, String key, String value) {
        if (player == null) return;
        if (key == null || key.isEmpty()) return;
        NamespacedKey namespacedKey = new NamespacedKey(HolyCore.getInstance(), key);
        player.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, value);
    }

    public static String getString(Player player, String key) {
        if (player == null) return null;
        if (key == null || key.isEmpty()) return null;
        NamespacedKey namespacedKey = new NamespacedKey(HolyCore.getInstance(), key);
        return player.getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING);
    }

    public static List<String> getPlayerPermissionList(Player user, String permissionPrefix) {
        // get list of all permissions under the prefix
        List<String> permissions = new java.util.ArrayList<>();

        for (org.bukkit.permissions.PermissionAttachmentInfo permission : user.getEffectivePermissions()) {
            if (permission.getPermission().startsWith(permissionPrefix)) {
                permissions.add(permission.getPermission().substring(permissionPrefix.length() + 1));
            }
        }

        return permissions;
    }
}
