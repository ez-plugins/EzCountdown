package com.skyblockexp.ezcountdown.api.event;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class CountdownStartEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Countdown countdown;

    public CountdownStartEvent(Countdown countdown) {
        this.countdown = countdown;
    }

    public Countdown getCountdown() {
        return countdown;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
