package org.beeholy.holyCore.listeners;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.beeholy.holyCore.HolyCore;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandEventListener implements Listener {
    @EventHandler
    public void onCommandSend(PlayerCommandSendEvent event) {
        Player player = event.getPlayer();
        if(player.isOp()) return;
        int playerWeight = getWeight(player);

        Set<String> allowedCommands = new HashSet<>();

        ConfigurationSection groups = HolyCore.getInstance().getConfig().getConfigurationSection("groups");
        if (groups == null) return;

        for (String groupName : groups.getKeys(false)) {

            int groupWeight = groups.getInt(groupName + ".weight");

            if (groupWeight <= playerWeight) {
                List<String> commands = groups.getStringList(groupName + ".commands");
                commands.forEach(cmd -> allowedCommands.add(cmd.toLowerCase()));
            }
        }

        event.getCommands().removeIf(cmd ->
                !allowedCommands.contains(cmd.toLowerCase())
        );
    }

    private int getWeight(Player player) {
        LuckPerms api = LuckPermsProvider.get();

        User user = api.getUserManager().getUser(player.getUniqueId());
        if (user == null) return 0;

        return user.getPrimaryGroup() != null
                ? api.getGroupManager()
                .getGroup(user.getPrimaryGroup())
                .getWeight()
                .orElse(0)
                : 0;
    }
}
