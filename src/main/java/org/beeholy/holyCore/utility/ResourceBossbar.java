package org.beeholy.holyCore.utility;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import org.beeholy.holyCore.HolyCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ResourceBossbar implements Listener {

    private final HolyCore plugin;

    private final Set<String> resourceWorlds = new HashSet<>();
    private BossBar bossBar;

    public ResourceBossbar() {
        this.plugin = HolyCore.getInstance();
        load();
    }

    public void load() {
        // Hide old bossbar from everyone before rebuilding
        if (bossBar != null) {
            Bukkit.getOnlinePlayers().forEach(p -> p.hideBossBar(bossBar));
        }

        resourceWorlds.clear();
        resourceWorlds.addAll(plugin.getConfig().getStringList("resource-worlds"));

        String rawMessage = plugin.getConfig().getString(
                "bossbar.message",
                "<red>This is a resource world: claiming is disabled"
        );

        Component message = MiniMessage.miniMessage().deserialize(rawMessage);

        BossBar.Color color = parseColor(
                plugin.getConfig().getString("bossbar.color", "RED")
        );

        BossBar.Overlay overlay = parseOverlay(
                plugin.getConfig().getString("bossbar.overlay", "PROGRESS")
        );

        float progress = (float) plugin.getConfig().getDouble("bossbar.progress", 1.0);
        progress = Math.max(0f, Math.min(1f, progress)); // clamp 0-1

        bossBar = BossBar.bossBar(message, progress, color, overlay);

        // Re-check all online players after rebuilding
        Bukkit.getOnlinePlayers().forEach(this::checkWorld);
    }

    private BossBar.Color parseColor(String input) {
        try {
            return BossBar.Color.valueOf(input.toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            return BossBar.Color.RED;
        }
    }

    private BossBar.Overlay parseOverlay(String input) {
        try {
            return BossBar.Overlay.valueOf(input.toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            return BossBar.Overlay.PROGRESS;
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        checkWorld(event.getPlayer());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        checkWorld(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (bossBar != null) {
            event.getPlayer().hideBossBar(bossBar);
        }
    }

    private void checkWorld(Player player) {
        if (bossBar == null) return;

        if (resourceWorlds.contains(player.getWorld().getName())) {
            player.showBossBar(bossBar);
        } else {
            player.hideBossBar(bossBar);
        }
    }
}
