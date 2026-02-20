package org.beeholy.holyCore;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.beeholy.holyCore.chat.Colors;
import org.beeholy.holyCore.chat.Gradients;
import org.beeholy.holyCore.chat.Tags;
import org.beeholy.holyCore.economy.FlyTime;
import org.beeholy.holyCore.hooks.LPHook;
import org.beeholy.holyCore.hooks.PAPIExpansion;
import org.beeholy.holyCore.items.Vouchers;
import org.beeholy.holyCore.listeners.*;
import org.beeholy.holyCore.listeners.enchants.BookListeners;
import org.beeholy.holyCore.listeners.enchants.BreakToolListeners;
import org.beeholy.holyCore.listeners.enchants.TelekinesisListeners;
import org.beeholy.holyCore.utility.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.print.Book;
import java.util.Map;

public final class HolyCore extends JavaPlugin {

    private static HolyCore instance;
    private final DBManager dbManager = DBManager.getInstance();
    private final MiniMessage mm = MiniMessage.miniMessage();

    public static HolyCore getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        // Startup
        saveDefaultConfig();

        // connect to db
        dbManager.connect();

        // Set up static helpers

        Tags.setup();
        Gradients.setup();
        Colors.setup();
        Vouchers.setup();
        FlyTime.setup();
        Crates.setup();
        Language.setup();
        Scoreboard.reload();
        Quests.reload();

        // resource bossbar
        ResourceBossbar resourceBossbar = new ResourceBossbar();
        Bukkit.getPluginManager().registerEvents(resourceBossbar, this);
        // register simple commands
        Commands.registerCommands(this);

        // Register events
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new GuiListeners(), this);
        Bukkit.getPluginManager().registerEvents(new VoucherListeners(), this);
        Bukkit.getPluginManager().registerEvents(new CratesListeners(), this);
        Bukkit.getPluginManager().registerEvents(new OverrideListeners(), this);
        //Bukkit.getPluginManager().registerEvents(new VoteListener(), this);
        Bukkit.getPluginManager().registerEvents(new SpawnerPickListners(), this);
        Bukkit.getPluginManager().registerEvents(new CaptureEggListeners(), this);
        Bukkit.getPluginManager().registerEvents(new CommandEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new DurabilityListener(), this);

        // Enchantment Listeners
        Bukkit.getPluginManager().registerEvents(new BookListeners(), this);
        Bukkit.getPluginManager().registerEvents(new TelekinesisListeners(), this);
        Bukkit.getPluginManager().registerEvents(new BreakToolListeners(), this);

        // Rank priority using luckperms api
        LuckPerms luckPerms = LuckPermsProvider.get();
        final Map<String, Integer> rankPriority = LPHook.getRankPriority();

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) { //
            new PAPIExpansion(this).register(); //
        }

        // Scheduled task, per second updates
        new BukkitRunnable() {

            @Override
            public void run() {

                for (Player player : Bukkit.getOnlinePlayers()) {
                    // Update Scoreboard
                    Scoreboard.updateScoreboard(player);
                    // Order, style and update tablist
                    Tablist.send(player);

                    FlyTime.tick(player);
                }

            }

        }.runTaskTimer(this, 20L, 20L);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        dbManager.close();
        this.getLogger().info("Disabling HolyCore");
    }
}
