package org.beeholy.holyCore.listeners;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.beeholy.holyCore.utility.Language;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener, ChatRenderer { // Implement the ChatRenderer and Listener interface

    MiniMessage mm = MiniMessage.miniMessage();

    // Listen for the AsyncChatEvent
    @EventHandler
    public void onChat(AsyncChatEvent event) {
        event.renderer(this); // Tell the event to use our renderer
    }

    // Override the render method
    @Override
    public Component render(Player source, Component sourceDisplayName, Component message, Audience viewer) {
        return TextUtils.deserializeAsPlayer(Language.get("chat_format"), source, message);
    }
}