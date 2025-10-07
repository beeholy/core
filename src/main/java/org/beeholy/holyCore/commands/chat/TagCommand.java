package org.beeholy.holyCore.commands.chat;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.beeholy.holyCore.chat.Gradients;
import org.beeholy.holyCore.gui.PermissionMenu;
import org.beeholy.holyCore.chat.Tags;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TagCommand implements BasicCommand {

    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();
        if (!(sender instanceof Player pp)) {
            sender.sendMessage("This command can only be run by a player.");
            return;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            PermissionMenu menu = new PermissionMenu(
                    TextUtils.deserialize("Tags"),
                    "tag",
                    "tag_name",
                    player,
                    "<tags:<data>/>");
            player.openInventory(menu.getInventory());
            return;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if(player.isOp()) {
                    player.sendMessage("Tags config reloaded");
                    Tags.reload();
                } else {
                    player.sendMessage("You have insufficient permissions.");
                }
                break;
            case "set":
                if (args.length < 2) {
                    player.sendMessage("Usage: /tag set <tagName>");
                    break;
                }
                String tagName = args[1];
                if (player.hasPermission("tag." + tagName)) {
                    boolean success = Tags.setPlayerTag(player, tagName);
                    if (success) {
                        player.sendMessage("Tag set to: " + tagName);
                    } else {
                        player.sendMessage("Tag not found.");
                    }
                } else {
                    player.sendMessage("You don't have permission for this tag.");
                }
                break;

            case "clear":
                boolean hadTag = Tags.setPlayerTag(player, "");
                if (hadTag) {
                    player.sendMessage("Your tag has been cleared.");
                } else {
                    player.sendMessage("You had no tag to clear.");
                }
                break;

            default:
                player.sendMessage("Unknown subcommand.");
        }
    }
    @Override
    public List<String> suggest(CommandSourceStack source, String[] args){
        if (args.length == 0 || args.length == 1) {
            if(source.getSender().isOp()) {
                return List.of("set", "clear", "reload");
            }
            return List.of("set", "clear");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            CommandSender sender = source.getSender();
            if (sender instanceof Player player) {
                return Tags.getPlayerTags(player);
            }
        }
        return List.of();
    }
}
