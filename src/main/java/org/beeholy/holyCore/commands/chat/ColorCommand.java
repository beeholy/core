package org.beeholy.holyCore.commands.chat;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.beeholy.holyCore.chat.Colors;
import org.beeholy.holyCore.gui.PermissionMenu;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ColorCommand implements BasicCommand {

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
                    TextUtils.deserialize("           ᴄʜᴀᴛ ᴄᴏʟᴏʀѕ"),
                    "color", "color_name",
                    pp,
                    "<colors:<data>><data></colors>");
            pp.openInventory(menu.getInventory());
            return;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (pp.isOp()) {
                    pp.sendMessage("Chat colors config reloaded");
                    Colors.reload();
                } else {
                    pp.sendMessage("Insufficent permissions");
                }
                break;
            case "set":
                if (args.length < 2) {
                    pp.sendMessage("Usage: /color set <name>");
                    break;
                }
                String colorName = args[1];
                if (pp.hasPermission("color." + colorName)) {
                    boolean success = Colors.setPlayerColor(pp, colorName);
                    if (success) {
                        pp.sendMessage("Chat color set to: " + colorName);
                    } else {
                        pp.sendMessage("Chat color not found.");
                    }
                } else {
                    pp.sendMessage("You don't have permission for this chat color.");
                }
                break;

            case "clear":
                boolean hadTag = Colors.setPlayerColor(pp, "");
                if (hadTag) {
                    pp.sendMessage("Your chat color has been cleared.");
                } else {
                    pp.sendMessage("You had no chat color to clear.");
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
                return Colors.getPlayerColors(player);
            }
        }
        return List.of();
    }
}
