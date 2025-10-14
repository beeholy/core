package org.beeholy.holyCore.commands.user;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.beeholy.holyCore.utility.Language;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class CooldownCommand implements BasicCommand {

    private final Map<UUID, Long> cooldownMap = new ConcurrentHashMap<>();
    private final long cooldownTimeMs;

    public CooldownCommand(long cooldownTimeMs) {
        this.cooldownTimeMs = cooldownTimeMs;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage("This command can only be run by a player.");
            return;
        }
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        if (cooldownMap.containsKey(uuid)) {
            long lastUse = cooldownMap.get(uuid);
            long elapsed = now - lastUse;

            if (elapsed < cooldownTimeMs) {
                long timeLeft = (cooldownTimeMs - elapsed) / 1000;
                player.sendMessage(TextUtils.deserialize(Language.get("cooldown_message"), String.valueOf(timeLeft)));
                return;
            }
        }

        cooldownMap.put(uuid, now);
        executeWithCooldown(player, args);
    }

    protected abstract void executeWithCooldown(Player player, String[] args);
}