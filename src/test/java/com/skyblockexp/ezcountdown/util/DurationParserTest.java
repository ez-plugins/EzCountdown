package com.skyblockexp.ezcountdown.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DurationParserTest {

    @Test
    public void parseSecondsOnly() {
        assertEquals(5L, DurationParser.parseToSeconds("5s"));
        assertEquals(5L, DurationParser.parseToSeconds(" 5 s ".trim()));
    }

    @Test
    public void parseMinutesAndSeconds() {
        assertEquals(65L, DurationParser.parseToSeconds("1m5s"));
        assertEquals(125L, DurationParser.parseToSeconds("2m5s"));
    }

    @Test
    public void parseHoursDays() {
        assertEquals(3600L, DurationParser.parseToSeconds("1h"));
        assertEquals(90000L, DurationParser.parseToSeconds("1d1h"));
        assertEquals(172800L, DurationParser.parseToSeconds("2d"));
    }

    @Test
    public void parseMixedOrderAndWhitespace() {
        assertEquals(3665L, DurationParser.parseToSeconds("1h 1m 5s"));
        assertEquals(90061L, DurationParser.parseToSeconds("1d 1h 1m 1s"));
    }

    @Test
    public void invalidInputsThrow() {
        assertThrows(IllegalArgumentException.class, () -> DurationParser.parseToSeconds(null));
        assertThrows(IllegalArgumentException.class, () -> DurationParser.parseToSeconds(""));
        assertThrows(IllegalArgumentException.class, () -> DurationParser.parseToSeconds("foobar"));
        assertThrows(IllegalArgumentException.class, () -> DurationParser.parseToSeconds("10x"));
    }
}
