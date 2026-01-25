package com.skyblockexp.ezcountdown.util;

public final class TimeFormat {

    private TimeFormat() {
    }

    public static TimeParts toParts(long totalSeconds) {
        long seconds = Math.max(totalSeconds, 0L);
        long days = seconds / 86400;
        seconds %= 86400;
        long hours = seconds / 3600;
        seconds %= 3600;
        long minutes = seconds / 60;
        seconds %= 60;
        return new TimeParts(days, hours, minutes, seconds);
    }

    public static String format(TimeParts parts) {
        return parts.days() + "d " + parts.hours() + "h " + parts.minutes() + "m " + parts.seconds() + "s";
    }

    public record TimeParts(long days, long hours, long minutes, long seconds) {
    }
}
