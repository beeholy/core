package org.beeholy.holyCore.commands.admin;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.beeholy.holyCore.utility.Language;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class HologramCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage("Command can only be ran by player");
            return;
        }
        if (args[0].isEmpty()) {
            source.getSender().sendMessage(TextUtils.deserialize(Language.get("hologram_command_usage")));
            return;
        }
        switch (args[0].toLowerCase()) {
            case "give":
                World world = player.getWorld();
                Location location = player.getLocation();
                location.setPitch(0);
                location.setYaw(0);

                TextDisplay display = world.spawn(location, TextDisplay.class, entity -> {
                    // customize the entity!
                    entity.text(Component.text(args[1], NamedTextColor.BLACK));
                    entity.setBillboard(Display.Billboard.VERTICAL); // pivot only around the vertical axis
                    entity.setBackgroundColor(Color.RED); // make the background red
                });
                break;
            case "reload":
                source.getSender().sendMessage(TextUtils.deserialize(Language.get("config_reload")));
                break;
        }

    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        return List.of();
    }

    @Override
    public @Nullable String permission() {
        return "hologram.admin";
    }
}
