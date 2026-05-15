package com.skyblockexp.ezcountdown.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TimeFormatTest {

    // ── legacy format(TimeParts) — no hiding ────────────────────────────────

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

    // ── format(TimeParts, FormatConfig) with hideLeadingZeros = false ───────

    @Test
    public void formatConfig_hideLeadingZerosFalse_keepsLeadingZeros() {
        var config = new TimeFormat.FormatConfig("{days}d {hours}h {minutes}m {seconds}s", false);
        TimeFormat.TimeParts parts = TimeFormat.toParts(3661); // 0d 1h 1m 1s
        assertEquals("0d 1h 1m 1s", TimeFormat.format(parts, config));
    }

    // ── format(TimeParts, FormatConfig) with hideLeadingZeros = true ────────

    @Test
    public void hideLeadingZeros_daysAndHoursZero_showsMinutesSeconds() {
        var config = new TimeFormat.FormatConfig("{days}d {hours}h {minutes}m {seconds}s", true);
        TimeFormat.TimeParts parts = TimeFormat.toParts(5 * 60 + 3); // 0d 0h 5m 3s
        assertEquals("5m 3s", TimeFormat.format(parts, config));
    }

    @Test
    public void hideLeadingZeros_onlyDaysZero_showsHoursOnward() {
        var config = new TimeFormat.FormatConfig("{days}d {hours}h {minutes}m {seconds}s", true);
        TimeFormat.TimeParts parts = TimeFormat.toParts(2 * 3600 + 30 * 60 + 15); // 0d 2h 30m 15s
        assertEquals("2h 30m 15s", TimeFormat.format(parts, config));
    }

    @Test
    public void hideLeadingZeros_allZeroExceptSeconds_showsSeconds() {
        var config = new TimeFormat.FormatConfig("{days}d {hours}h {minutes}m {seconds}s", true);
        TimeFormat.TimeParts parts = TimeFormat.toParts(45); // 0d 0h 0m 45s
        assertEquals("45s", TimeFormat.format(parts, config));
    }

    @Test
    public void hideLeadingZeros_allZero_showsZeroSeconds() {
        var config = new TimeFormat.FormatConfig("{days}d {hours}h {minutes}m {seconds}s", true);
        TimeFormat.TimeParts parts = TimeFormat.toParts(0); // 0d 0h 0m 0s
        assertEquals("0s", TimeFormat.format(parts, config));
    }

    @Test
    public void hideLeadingZeros_nothingToHide_returnsFull() {
        var config = new TimeFormat.FormatConfig("{days}d {hours}h {minutes}m {seconds}s", true);
        long total = 2L * 86400 + 5 * 3600 + 3 * 60 + 7;
        TimeFormat.TimeParts parts = TimeFormat.toParts(total); // 2d 5h 3m 7s
        assertEquals("2d 5h 3m 7s", TimeFormat.format(parts, config));
    }

    @Test
    public void hideLeadingZeros_customPattern_worksCorrectly() {
        var config = new TimeFormat.FormatConfig("{hours}h {minutes}m {seconds}s", true);
        TimeFormat.TimeParts parts = TimeFormat.toParts(3 * 60 + 22); // 0h 3m 22s
        assertEquals("3m 22s", TimeFormat.format(parts, config));
    }

    @Test
    public void nullOrBlankPattern_fallsBackToDefault() {
        var config = new TimeFormat.FormatConfig(null, false);
        TimeFormat.TimeParts parts = TimeFormat.toParts(3661);
        assertEquals("0d 1h 1m 1s", TimeFormat.format(parts, config));
    }
}
