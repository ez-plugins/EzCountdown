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
                .addDisplayType(com.skyblockexp.ezcountdown.display.DisplayType.ACTION_BAR)
                .recurringDate(12, 25, t)
                .build();

        assertNotNull(c);
        assertEquals(CountdownType.RECURRING, c.getType());
        assertEquals(12, c.getRecurringMonth());
        assertEquals(25, c.getRecurringDay());
        assertEquals(t, c.getRecurringTime());
    }

    @Test
    void buildWithoutDisplayTypes_throws() {
        CountdownBuilder b = CountdownBuilder.builder("nodisplay")
            .type(CountdownType.DURATION)
            .addDisplayType(com.skyblockexp.ezcountdown.display.DisplayType.ACTION_BAR);

        Countdown c = b.build();
        assertNotNull(c);
        assertTrue(c.getDisplayTypes().contains(com.skyblockexp.ezcountdown.display.DisplayType.ACTION_BAR));
    }

    @Test
    void buildWithoutType_throws() throws Exception {
        CountdownBuilder b = CountdownBuilder.builder("notype");

        // force the private `type` field to null to test the exception path
        java.lang.reflect.Field f = CountdownBuilder.class.getDeclaredField("type");
        f.setAccessible(true);
        f.set(b, null);

        assertThrows(NoCountdownTypeSetException.class, b::build);
    }
}
