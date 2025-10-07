package org.beeholy.holyCore.commands.chat;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.beeholy.holyCore.chat.Gradients;
import org.beeholy.holyCore.gui.PermissionMenu;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GradientCommand implements BasicCommand {

    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();
        if (!(sender instanceof Player pp)) {
            sender.sendMessage("This command can only be run by a player.");
            return;
        }

        if (args.length == 0) {
            PermissionMenu menu = new PermissionMenu(
                    TextUtils.deserialize("Gradients"),
                    "gradient",
                    "gradient_name",
                    pp,
                    "<gradients:<data>><data></reset>");
            pp.openInventory(menu.getInventory());
            return;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (pp.isOp()) {
                    pp.sendMessage("Gradients config reloaded");
                    Gradients.reload();
                } else {
                    pp.sendMessage("Insufficient permissions");
                }
                break;
            case "set":
                if (args.length < 2) {
                    pp.sendMessage("Usage: /gradient set <name>");
                    break;
                }
                String gradientName = args[1];
                if (pp.hasPermission("gradient." + gradientName)) {
                    boolean success = Gradients.setPlayerGradient(pp, gradientName);
                    if (success) {
                        pp.sendMessage("Gradient set to: " + gradientName);
                    } else {
                        pp.sendMessage("Gradient not found.");
                    }
                } else {
                    pp.sendMessage("You don't have permission for this gradient.");
                }
                break;

            case "clear":
                boolean hadGradient = Gradients.setPlayerGradient(pp, "");
                if (hadGradient) {
                    pp.sendMessage("Your gradient has been cleared.");
                } else {
                    pp.sendMessage("You had no gradient to clear.");
                }
                break;

            default:
                pp.sendMessage("Unknown subcommand.");
        }
    }

    @Override
    public List<String> suggest(CommandSourceStack source, String[] args) {
        if (args.length == 0 || args.length == 1) {
            if (source.getSender().isOp()) {
                return List.of("set", "clear", "reload");
            }
            return List.of("set", "clear");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            CommandSender sender = source.getSender();
            if (sender instanceof Player player) {
                return Gradients.getPlayerGradients(player);
            }
        }
        return List.of();
    }
}
