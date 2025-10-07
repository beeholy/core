package org.beeholy.holyCore.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

public class AliasCommand implements BasicCommand {

    private final String command;
    private final String exe;
    private final String permission;

    public AliasCommand(String command, String exe, String permission) {
        this.permission = permission;
        this.command = command;
        this.exe = exe;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage("Command can only be ran by players");
            return;
        }
        CommandSender commandSender = Bukkit.getConsoleSender();
        Bukkit.getServer().dispatchCommand(commandSender, exe.replace("<player>", player.getName()));
    }

    @Override
    public @Nullable String permission() {
        return !this.permission.equals("") ? this.permission : BasicCommand.super.permission();
    }
}
