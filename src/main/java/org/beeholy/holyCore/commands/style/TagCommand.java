package org.beeholy.holyCore.commands.style;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.beeholy.holyCore.chat.Tags;
import org.beeholy.holyCore.gui.PermissionMenu;
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

        if (args.length == 0) {
            PermissionMenu menu = new PermissionMenu(
                    TextUtils.deserialize("              ᴄʜᴀᴛ ᴛᴀɢѕ"),
                    "tag",
                    "tag_name",
                    pp,
                    "<tags:<data>/>");
            pp.openInventory(menu.getInventory());
            return;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (pp.isOp()) {
                    pp.sendMessage("Tags config reloaded");
                    Tags.reload();
                } else {
                    pp.sendMessage("You have insufficient permissions.");
                }
                break;
            case "set":
                if (args.length < 2) {
                    pp.sendMessage("Usage: /tag set <tagName>");
                    break;
                }
                String tagName = args[1];
                if (pp.hasPermission("tag." + tagName)) {
                    boolean success = Tags.setPlayerTag(pp, tagName);
                    if (success) {
                        pp.sendMessage("Tag set to: " + tagName);
                    } else {
                        pp.sendMessage("Tag not found.");
                    }
                } else {
                    pp.sendMessage("You don't have permission for this command.");
                }
                break;

            case "edit":
                if (args.length < 3) {
                    pp.sendMessage("Usage: /tag edit <tagName> <NewTag>");
                    break;
                }
                String editTagName = args[1];
                String editTag = args[2];
                if (pp.hasPermission("tag.admin.edit")) {
                    boolean success = Tags.editTag(editTagName, editTag);
                    if (success) {
                        pp.sendMessage("Tag edited");
                    } else {
                        pp.sendMessage("Tag doesnt exist");
                    }
                } else {
                    pp.sendMessage("You don't have permission for this command.");
                }
                break;

            case "create":
                if (args.length < 3) {
                    pp.sendMessage("Usage: /tag create <tagName> <tag>");
                    break;
                }
                String newTagName = args[1];
                String newTag = args[2];
                if (pp.hasPermission("tag.admin.create")) {
                    boolean success = Tags.addTag(newTagName, newTag);
                    if (success) {
                        pp.sendMessage("Tag created");
                    } else {
                        pp.sendMessage("Tag name already exists");
                    }
                } else {
                    pp.sendMessage("You don't have permission for this command.");
                }
                break;

            case "remove":
                if (args.length < 2) {
                    pp.sendMessage("Usage: /tag remove <tagName>");
                    break;
                }
                String tagRemName = args[1];
                if (pp.hasPermission("tag.admin.remove")) {
                    boolean success = Tags.removeTag(tagRemName);
                    if (success) {
                        pp.sendMessage("Tag removed");
                    } else {
                        pp.sendMessage("Tag not found.");
                    }
                } else {
                    pp.sendMessage("You don't have permission for this tag.");
                }
                break;

            case "clear":
                boolean hadTag = Tags.setPlayerTag(pp, "");
                if (hadTag) {
                    pp.sendMessage("Your tag has been cleared.");
                } else {
                    pp.sendMessage("You had no tag to clear.");
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
                return List.of("set", "clear", "edit", "create", "remove", "reload");
            }
            return List.of("set", "clear");
        }
        if ((args.length == 2 && args[0].equalsIgnoreCase("set"))) {
            CommandSender sender = source.getSender();
            if (sender instanceof Player player) {
                return Tags.getPlayerTags(player);
            }
        }
        if((args.length == 2 && args[0].equalsIgnoreCase("remove")) ||
            (args.length == 2 && args[0].equalsIgnoreCase("edit"))) {
            return Tags.getTagNames();
        }
        if(args.length == 3 && args[0].equalsIgnoreCase("edit")){
            CommandSender sender = source.getSender();
            if (sender instanceof Player player) {
                return List.of(Tags.getTag(args[1].toLowerCase()));
            }
        }
        return List.of();
    }
}
