package org.beeholy.holyCore.utility;

import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.beeholy.holyCore.HolyCore;
import org.beeholy.holyCore.commands.*;
import org.beeholy.holyCore.commands.chat.ColorCommand;
import org.beeholy.holyCore.commands.chat.GradientCommand;
import org.beeholy.holyCore.commands.chat.TagCommand;
import org.beeholy.holyCore.commands.mod.ModCommands;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class Commands {

    public static void registerCommands(Plugin plugin) {
        LifecycleEventManager<Plugin> manager = plugin.getLifecycleManager();

        // Get commands from commands.yml file
        File commandsFile = new File(HolyCore.getInstance().getDataFolder(), "commands.yml");
        if (!commandsFile.exists()) {
            commandsFile.getParentFile().mkdirs();
            HolyCore.getInstance().saveResource("commands.yml", false);
        }
        FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);


        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final io.papermc.paper.command.brigadier.Commands commands = event.registrar();
            commands.register("signup", "Sign up for holymc.uk web", new SignupCommand());
            commands.register("tag", "Tags commands", new TagCommand());
            commands.register("gradient", "Gradients command", new GradientCommand());
            commands.register("color", "Chat colors command", new ColorCommand());
            commands.register("rtp", "Random teleport command", new RTPCommand(10_000));
            commands.register("voucher", "Voucher commands", new VoucherCommand());
            commands.register("fly", "Fly commands", new FlyCommand());
            commands.register("hologram", "Fly commands", new HologramCommand());
            commands.register("reload", "Fly commands", new ReloadCommand());
            commands.register("crates", "Crates commands", new CratesCommand());
            commands.register("item", "Item factory commands", new ItemCommand());

            ModCommands modCommands = new ModCommands();

            commands.register(modCommands.banCommand);
            commands.register(modCommands.kickCommand);
            commands.register(modCommands.muteCommand);
            commands.register(modCommands.statCommand);

            for (String command : commandsConfig.getKeys(false)) {
                String permission = commandsConfig.getString(command + ".permission");
                String description = commandsConfig.getString(command + ".desc");
                String exe = commandsConfig.getString(command + ".run");
                if (permission == null) {
                    commands.register(command, description, new AliasCommand(command, exe, ""));
                } else {
                    commands.register(command, description, new AliasCommand(command, exe, permission));
                }
            }
        });
    }
}
