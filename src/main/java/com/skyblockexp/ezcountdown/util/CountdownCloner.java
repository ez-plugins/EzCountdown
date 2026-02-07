package com.skyblockexp.ezcountdown.util;

import com.skyblockexp.ezcountdown.api.model.Countdown;

import java.time.Instant;

public final class CountdownCloner {
    private CountdownCloner() {}

    public static void copyRuntimeFields(Countdown src, Countdown dest) {
        dest.setDurationSeconds(src.getDurationSeconds());
        dest.setTargetInstant(src.getTargetInstant() == null ? null : Instant.from(src.getTargetInstant()));
        dest.setRecurringMonth(src.getRecurringMonth());
        dest.setRecurringDay(src.getRecurringDay());
        dest.setRecurringTime(src.getRecurringTime());
        dest.setRunning(src.isRunning());
    }
}
