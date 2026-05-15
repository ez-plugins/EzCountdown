package com.skyblockexp.ezcountdown.util;

public final class TimeFormat {

    public static final String DEFAULT_PATTERN = "{days}d {hours}h {minutes}m {seconds}s";

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

    /**
     * Formats using the default pattern with no leading-zero suppression.
     * Kept for backward compatibility.
     */
    public static String format(TimeParts parts) {
        return format(parts, FormatConfig.DEFAULT);
    }

    /**
     * Formats {@code parts} according to the given {@link FormatConfig}.
     * <p>
     * The pattern supports the tokens {@code {days}}, {@code {hours}},
     * {@code {minutes}}, {@code {seconds}}.  When
     * {@link FormatConfig#hideLeadingZeros()} is {@code true}, leading
     * space-delimited segments whose unit value is zero are omitted, but at
     * least the last segment is always kept.
     */
    public static String format(TimeParts parts, FormatConfig config) {
        String pattern = (config.pattern() == null || config.pattern().isBlank())
                ? DEFAULT_PATTERN : config.pattern();
        if (!config.hideLeadingZeros()) {
            return pattern
                    .replace("{days}", String.valueOf(parts.days()))
                    .replace("{hours}", String.valueOf(parts.hours()))
                    .replace("{minutes}", String.valueOf(parts.minutes()))
                    .replace("{seconds}", String.valueOf(parts.seconds()));
        }
        // Split on spaces; drop leading segments whose single unit value is 0.
        String[] segments = pattern.split(" ");
        String[] tokens = {"{days}", "{hours}", "{minutes}", "{seconds}"};
        long[] values = {parts.days(), parts.hours(), parts.minutes(), parts.seconds()};
        boolean leading = true;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < segments.length; i++) {
            String seg = segments[i];
            boolean skip = false;
            if (leading && i < segments.length - 1) {
                for (int u = 0; u < tokens.length; u++) {
                    if (seg.contains(tokens[u]) && values[u] == 0) {
                        skip = true;
                        break;
                    }
                }
            }
            if (skip) continue;
            leading = false;
            if (sb.length() > 0) sb.append(' ');
            sb.append(seg);
        }
        return sb.toString()
                .replace("{days}", String.valueOf(parts.days()))
                .replace("{hours}", String.valueOf(parts.hours()))
                .replace("{minutes}", String.valueOf(parts.minutes()))
                .replace("{seconds}", String.valueOf(parts.seconds()));
    }

    public record TimeParts(long days, long hours, long minutes, long seconds) {
    }

    public record FormatConfig(String pattern, boolean hideLeadingZeros) {
        /** Default: full pattern, no leading-zero suppression (preserves old behaviour). */
        public static final FormatConfig DEFAULT = new FormatConfig(DEFAULT_PATTERN, false);
    }
}
