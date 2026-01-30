package com.skyblockexp.ezcountdown.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TimeFormatTest {

    @Test
    public void zeroSeconds() {
        TimeFormat.TimeParts parts = TimeFormat.toParts(0);
        assertEquals(0, parts.days());
        assertEquals(0, parts.hours());
        assertEquals(0, parts.minutes());
        assertEquals(0, parts.seconds());
        assertEquals("0d 0h 0m 0s", TimeFormat.format(parts));
    }

    @Test
    public void simpleConversion() {
        TimeFormat.TimeParts parts = TimeFormat.toParts(3661);
        assertEquals(0, parts.days());
        assertEquals(1, parts.hours());
        assertEquals(1, parts.minutes());
        assertEquals(1, parts.seconds());
        assertEquals("0d 1h 1m 1s", TimeFormat.format(parts));
    }

    @Test
    public void largeValues() {
        long total = 2L * 86400 + 5 * 3600 + 3 * 60 + 7; // 2 days, 5 hours, 3 minutes, 7 seconds
        TimeFormat.TimeParts parts = TimeFormat.toParts(total);
        assertEquals(2, parts.days());
        assertEquals(5, parts.hours());
        assertEquals(3, parts.minutes());
        assertEquals(7, parts.seconds());
        assertEquals("2d 5h 3m 7s", TimeFormat.format(parts));
    }

    @Test
    public void negativeInputClampedToZero() {
        TimeFormat.TimeParts parts = TimeFormat.toParts(-100);
        assertEquals(0, parts.days());
        assertEquals(0, parts.hours());
        assertEquals(0, parts.minutes());
        assertEquals(0, parts.seconds());
    }
}
