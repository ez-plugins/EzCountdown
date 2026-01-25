package com.skyblockexp.ezcountdown.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DurationParser {

    private static final Pattern TOKEN_PATTERN = Pattern.compile("(\\d+)([smhd])", Pattern.CASE_INSENSITIVE);

    private DurationParser() {
    }

    public static long parseToSeconds(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Duration input is empty.");
        }
        Matcher matcher = TOKEN_PATTERN.matcher(input.replace(" ", ""));
        long totalSeconds = 0L;
        int matches = 0;
        while (matcher.find()) {
            matches++;
            long value = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2).toLowerCase();
            switch (unit) {
                case "s":
                    totalSeconds += value;
                    break;
                case "m":
                    totalSeconds += value * 60L;
                    break;
                case "h":
                    totalSeconds += value * 3600L;
                    break;
                case "d":
                    totalSeconds += value * 86400L;
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported duration unit: " + unit);
            }
        }
        if (matches == 0) {
            throw new IllegalArgumentException("Invalid duration format. Use values like 10m, 2h, or 1d4h.");
        }
        return totalSeconds;
    }
}
