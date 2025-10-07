package org.beeholy.holyCore.events;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PunishEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private String player;
    private String punisher;
    private String punishment;
    private String duration;
    private String reason;

    public PunishEvent(String player, String punisher, String punishment, String duration, String reason) {
        super(false);
        this.player = player;
        this.punisher = punisher;
        this.punishment = punishment;
        this.duration = duration;
        this.reason = reason;
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

    public String getReason(){
        return reason;
    }

    public String getDuration() {
        return duration;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
