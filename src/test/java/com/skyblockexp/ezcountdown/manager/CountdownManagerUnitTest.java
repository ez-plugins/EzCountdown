package com.skyblockexp.ezcountdown.manager;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

public class CountdownManagerUnitTest {

    @Test
    public void resolveNextRecurringTargetAdvancesWhenPast() {
        Countdown c = new Countdown("r1", CountdownType.RECURRING, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "{formatted}", "start", "end", java.util.List.of(), ZoneId.of("UTC"));
        c.setRecurringMonth(1);
        c.setRecurringDay(1);
        c.setRecurringTime(LocalTime.of(0, 0));

        // if now is just after the recurring time on the same day, the next should be next year
        Instant now = Instant.parse("2026-01-01T01:00:00Z");
        Instant next = c.resolveNextRecurringTarget(now);
        assertTrue(next.isAfter(now));
        // expected year is 2027 at 00:00 UTC
        assertEquals("2027-01-01T00:00:00Z", next.toString());
    }
}
