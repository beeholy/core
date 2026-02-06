package me.buttholy.holy.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FlyCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("start")) {
            stack.getSender().sendRichMessage("<rainbow>Fun activated!");
        } else if (args.length == 0) {
            if (stack.getSender() instanceof Player) {
                Player player = (Player) stack.getSender();

                if (player.getAllowFlight()) {
                    player.setAllowFlight(false);
                    player.sendMessage("Flight disabled.");
                } else {
                    player.setAllowFlight(true);
                    player.sendMessage("Flight enabled.");
                }
            }
        }
    }
}
//public class FlyCommand implements CommandExecutor {
//    @Override
//    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
//        if(!(sender instanceof Player)){
//            sender.sendMessage("Only player can use this command.");
//            return true;
//        }
//
//        Player player = (Player) sender;
//
//        if(player.getAllowFlight()) {
//            player.setAllowFlight(false);
//            player.sendMessage(ChatColor.RED + "Flight disabled.");
//        } else {
//            player.setAllowFlight(true);
//            player.sendMessage(ChatColor.GREEN + "Flight enabled.");
//        }
//        return true;
//    }
//}
