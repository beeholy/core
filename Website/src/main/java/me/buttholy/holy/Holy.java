package me.buttholy.holy;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.buttholy.holy.commands.FlyCommand;
import me.buttholy.holy.gui.GUIManager;
import me.buttholy.holy.listeners.PlayerJoinListener;
import me.buttholy.holy.listeners.captureEggListeners;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.DriverManager;
import java.sql.SQLException;

public final class Holy extends JavaPlugin implements PluginBootstrap {
    private GUIManager manager;

    @Override
    public void onEnable() {
        // Connect to DB
        try (var conn = DriverManager.getConnection("jdbc:sqlite:plugins/HolyMod/database.db");
             var stmt = conn.createStatement()) {
            // create a new table
            var sql = "CREATE TABLE \"users\" (\n" +
                    "\t\"uuid\"\tTEXT UNIQUE,\n" +
                    "\t\"username\"\tTEXT UNIQUE,\n" +
                    "\t\"money\"\tINTEGER NOT NULL DEFAULT 0,\n" +
                    "\tPRIMARY KEY(\"uuid\")\n" +
                    ");";
            stmt.execute(sql);
            getServer().getLogger().info("Users table created.");
        } catch (SQLException e) {
            getServer().getLogger().severe(e.getMessage());
        }

        // Plugin startup logic
        manager = new GUIManager(this);

        // Register Events
        getServer().getPluginManager().registerEvents(new captureEggListeners(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

        // Register Commands
       /* getCommand("menu").setExecutor(this);
        getCommand("fly").setExecutor(new FlyCommand());*/

    }

    @Override
    public void onDisable() {
        // Plugin shutdown login
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (label.equals("menu")) {
                /*MenuGUI menu = new MenuGUI("Menu",27,"Healing Stuffs");
                //Player player = (Player) sender;
                manager.openGUI(player, menu);*/
                var sql = "UPDATE users SET money = money + 20 WHERE uuid = '" + player.getUniqueId() + "';";
                try (var conn = DriverManager.getConnection("jdbc:sqlite:plugins/HolyMod/database.db");
                     var stmt = conn.createStatement()) {
                    // create a new table
                    stmt.execute(sql);
                } catch (SQLException e) {
                    getLogger().severe(e.getMessage());
                }
                player.sendMessage("Table made");
            }
        } else {
            sender.sendMessage("Command must come from a player");
        }
        return true;
    }

    @Override
    public void bootstrap(BootstrapContext bootstrapContext) {
        // Loads before server
        LifecycleEventManager<BootstrapContext> manager = bootstrapContext.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register("fly", "some help description string", new FlyCommand());
        });
    }
}
