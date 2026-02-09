package com.skyblockexp.ezcountdown.type;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownBuilder;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

public class RecurringAlignmentTest {

    @Test
    public void nextOccurrenceUtcTwoHourInterval() {
        Countdown cd = CountdownBuilder.builder("test")
                .type(com.skyblockexp.ezcountdown.api.model.CountdownType.RECURRING)
                .alignToClock(true)
                .alignInterval("2h")
                .zoneId(ZoneId.of("UTC"))
                .build();

        // 2026-02-09T01:15Z -> next aligned 02:00Z
        ZonedDateTime sample = ZonedDateTime.of(2026, 2, 9, 1, 15, 0, 0, ZoneId.of("UTC"));
        Instant next = cd.resolveNextRecurringTarget(sample.toInstant());
        ZonedDateTime expected = ZonedDateTime.of(2026, 2, 9, 2, 0, 0, 0, ZoneId.of("UTC"));
        assertEquals(expected.toInstant(), next);
    }

    @Test
    public void nextOccurrenceLondonOneDayInterval() {
        Countdown cd = CountdownBuilder.builder("daily")
                .type(com.skyblockexp.ezcountdown.api.model.CountdownType.RECURRING)
                .alignToClock(true)
                .alignInterval("1d")
                .zoneId(ZoneId.of("Europe/London"))
                .build();

        // Choose a date that is not on midnight local time
        ZonedDateTime sample = ZonedDateTime.of(2026, 2, 9, 10, 30, 0, 0, ZoneId.of("Europe/London"));
        Instant next = cd.resolveNextRecurringTarget(sample.toInstant());
        ZonedDateTime expected = sample.toLocalDate().atStartOfDay(ZoneId.of("Europe/London")).plusDays(1);
        assertEquals(expected.toInstant(), next);
    }
}
