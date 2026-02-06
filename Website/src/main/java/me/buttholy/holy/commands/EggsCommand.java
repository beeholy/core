package me.buttholy.holy.commands;

import me.buttholy.holy.customitem.CaptureEgg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EggsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(!(sender instanceof Player)) {
            sender.sendMessage("Command must come from player");
            return true;
        }
        Player player = (Player) sender;
        int firstEmpty = player.getInventory().firstEmpty();
        if(firstEmpty != -1){
            player.getInventory().setItem(firstEmpty, new CaptureEgg());
        }
        return true;
    }
}
