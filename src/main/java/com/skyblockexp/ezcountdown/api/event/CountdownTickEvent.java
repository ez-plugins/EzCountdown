package com.skyblockexp.ezcountdown.api.event;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class CountdownTickEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Countdown countdown;
    private final long remainingSeconds;

    public CountdownTickEvent(Countdown countdown, long remainingSeconds) {
        this.countdown = countdown;
        this.remainingSeconds = remainingSeconds;
    }

    public Countdown getCountdown() {
        return countdown;
    }

    public long getRemainingSeconds() {
        return remainingSeconds;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
