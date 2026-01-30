package com.skyblockexp.ezcountdown.api.model;

import com.skyblockexp.ezcountdown.display.DisplayType;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class CountdownBuilderTest {

    @Test
    void buildDurationCountdown_setsPropertiesAndDuration() {
        Countdown c = CountdownBuilder.builder("test-duration")
                .type(CountdownType.DURATION)
                .displayTypes(EnumSet.of(DisplayType.ACTION_BAR))
                .updateIntervalSeconds(2)
                .formatMessage("fmt.key")
                .startMessage("Starting")
                .endMessage("Ended")
                .zoneId(ZoneId.of("UTC"))
                .duration(Duration.ofMinutes(5))
                .build();

        assertNotNull(c);
        assertEquals("test-duration", c.getName());
        assertEquals(CountdownType.DURATION, c.getType());
        assertEquals(1, c.getDisplayTypes().size());
        assertTrue(c.getDisplayTypes().contains(DisplayType.ACTION_BAR));
        assertEquals(2, c.getUpdateIntervalSeconds());
        assertEquals("fmt.key", c.getFormatMessage());
        assertEquals("Starting", c.getStartMessage());
        assertEquals("Ended", c.getEndMessage());
        assertEquals(5 * 60L, c.getDurationSeconds());
        assertEquals(ZoneId.of("UTC"), c.getZoneId());
    }

    @Test
    void buildRecurringCountdown_setsRecurringFields() {
        LocalTime t = LocalTime.of(12, 30);
        Countdown c = CountdownBuilder.builder("recurring")
                .type(CountdownType.RECURRING)
                .recurringDate(12, 25, t)
                .build();

        assertNotNull(c);
        assertEquals(CountdownType.RECURRING, c.getType());
        assertEquals(12, c.getRecurringMonth());
        assertEquals(25, c.getRecurringDay());
        assertEquals(t, c.getRecurringTime());
    }
}
