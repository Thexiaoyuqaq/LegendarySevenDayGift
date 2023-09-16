package com.gyzer.sevendaygift.API.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ClaimGiftEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private int day;
    private Player p;

    public ClaimGiftEvent(int day, Player p) {
        this.day = day;
        this.p = p;
    }

    public int getDay() {
        return day;
    }

    public Player getPlayer() {
        return p;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
