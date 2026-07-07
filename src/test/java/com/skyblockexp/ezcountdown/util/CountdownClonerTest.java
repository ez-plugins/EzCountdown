package com.skyblockexp.ezcountdown.util;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownBuilder;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CountdownClonerTest {

    @Test
    void copyRuntimeFieldsCopiesAllValues() {
        Countdown src = CountdownBuilder.builder("src")
                .type(CountdownType.DURATION)
                .durationSeconds(300)
                .build();
        src.setTargetInstant(Instant.parse("2026-01-01T00:00:30Z"));
        src.setRecurringMonth(12);
        src.setRecurringDay(25);
        src.setRecurringTime(LocalTime.of(8, 30));
        src.setStartSound("BLOCK_NOTE_BLOCK_PLING");
        src.setEndSound("ENTITY_PLAYER_LEVELUP");
        src.setRunning(true);

        Countdown dest = CountdownBuilder.builder("dest")
                .type(CountdownType.MANUAL)
                .durationSeconds(10)
                .build();

        CountdownCloner.copyRuntimeFields(src, dest);

        assertEquals(300, dest.getDurationSeconds());
        assertEquals(src.getTargetInstant(), dest.getTargetInstant());
        assertEquals(12, dest.getRecurringMonth());
        assertEquals(25, dest.getRecurringDay());
        assertEquals(LocalTime.of(8, 30), dest.getRecurringTime());
        assertEquals("BLOCK_NOTE_BLOCK_PLING", dest.getStartSound());
        assertEquals("ENTITY_PLAYER_LEVELUP", dest.getEndSound());
        assertTrue(dest.isRunning());
    }

    @Test
    void copyRuntimeFieldsHandlesNullTargetInstant() {
        Countdown src = CountdownBuilder.builder("src-null")
                .type(CountdownType.DURATION)
                .durationSeconds(45)
                .build();
        src.setTargetInstant(null);

        Countdown dest = CountdownBuilder.builder("dest-null")
                .type(CountdownType.DURATION)
                .durationSeconds(1)
                .build();
        dest.setTargetInstant(Instant.parse("2026-01-01T00:00:01Z"));

        CountdownCloner.copyRuntimeFields(src, dest);

        assertNull(dest.getTargetInstant());
        assertEquals(45, dest.getDurationSeconds());
    }
}
