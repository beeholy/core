package org.beeholy.holyCore.listeners;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.beeholy.holyCore.HolyCore;
import org.beeholy.holyCore.utility.Language;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

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
        ItemStack heldItem = source.getInventory().getItemInMainHand();
        Component newMessage = message;
        int heldAmount = heldItem.getAmount();

        NamespacedKey key = new NamespacedKey(HolyCore.getInstance(), "hiddenRank");
        if(source.getPersistentDataContainer().has(key)){
            String withoutTagFormat = Language.get("chat_format").replace("<player_rank>", "");
            message =  TextUtils.deserializeAsPlayer(withoutTagFormat, source, newMessage);
        } else {
            message = TextUtils.deserializeAsPlayer(Language.get("chat_format"), source, newMessage);
        }
        Component replacedItem;
        if (heldAmount >= 1) {

            if (heldAmount > 1) {
                replacedItem =
                                Component.text("[" + heldAmount + "x", NamedTextColor.GRAY)
                                .append(heldItem.displayName())
                                .append(Component.text("]", NamedTextColor.GRAY))
                                .hoverEvent(heldItem.asHoverEvent());
            } else if(heldAmount == 1){
                replacedItem = heldItem.displayName()
                                .hoverEvent(heldItem.asHoverEvent());
            } else {
                replacedItem = null;
            }

            newMessage = message.replaceText(builder ->
                    builder.matchLiteral("[i]").replacement(replacedItem)
            );
        } else {
            replacedItem = null;
            newMessage = message;
        }

        return newMessage;

    }
}