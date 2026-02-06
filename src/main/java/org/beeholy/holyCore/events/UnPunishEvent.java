package org.beeholy.holyCore.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UnPunishEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final String player;
    private final String punisher;
    private final String punishment;

    public UnPunishEvent(String player, String punisher, String punishment) {
        super(false);
        this.player = player;
        this.punisher = punisher;
        this.punishment = punishment;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public String getPlayer() {
        return player;
    }

    public String getPunisher() {
        return punisher;
    }

    public String getPunishment() {
        return punishment;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
