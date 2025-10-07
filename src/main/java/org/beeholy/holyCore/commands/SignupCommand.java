package org.beeholy.holyCore.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.beeholy.holyCore.utility.DatabaseManager;
import org.beeholy.holyCore.HolyCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public class SignupCommand extends CooldownCommand {

    public SignupCommand() {
        super(10_000); // 10 seconds cooldown
    }

    @Override
    protected void executeWithCooldown(Player player, String[] args) {
        String keyString = UUID.randomUUID().toString().substring(0, 8);

        player.sendMessage(MiniMessage.miniMessage().deserialize(
                "\nGo to holymc.uk/signup or click " +
                        "<click:open_url:'https://holymc.uk/?signup&username=" + player.getName() + "&key=" + keyString + "'><u>this link</u></click>." +
                        "\nCode: <red>(do not share this with anybody)</red> " + keyString +
                        "\nExpires in 5min \n"
        ));
        Bukkit.getScheduler().runTaskAsynchronously(HolyCore.getInstance(), () -> {
            DatabaseManager.getInstance().createSignupLink(player.getUniqueId(), keyString);
        });
    }

    @Override
    public @Nullable String permission() {
        return "signup";
    }
}
