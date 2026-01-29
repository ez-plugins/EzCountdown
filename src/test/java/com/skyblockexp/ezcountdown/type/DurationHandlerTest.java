package com.skyblockexp.ezcountdown.type;

import com.skyblockexp.ezcountdown.display.DisplayType;
import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.manager.CountdownDefaults;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

public class DurationHandlerTest {
    private final DurationHandler handler = new DurationHandler();
    private final CountdownDefaults defaults = new CountdownDefaults(EnumSet.noneOf(DisplayType.class), 1, null, "", "", "", true, ZoneId.of("UTC"));

    @Test
    public void configureFromCreateArgs_setsDurationAndRunning() {
        Countdown cd = new Countdown("t", handler.getType(), defaults.displayTypes(), defaults.updateIntervalSeconds(), null, "", "", "", java.util.List.of(), defaults.zoneId());
        handler.configureFromCreateArgs(cd, new String[]{"1m"}, defaults);
        assertEquals(60, cd.getDurationSeconds());
        assertTrue(cd.isRunning());
    }

    @Test
    public void onStart_setsTargetRelativeToNow() {
        Countdown cd = new Countdown("t2", handler.getType(), defaults.displayTypes(), defaults.updateIntervalSeconds(), null, "", "", "", java.util.List.of(), defaults.zoneId());
        cd.setDurationSeconds(10);
        Instant now = Instant.parse("2026-01-01T00:00:00Z");
        handler.onStart(cd, now);
        assertEquals(Instant.parse("2026-01-01T00:00:10Z"), cd.getTargetInstant());
    }

    @Test
    public void tryApplyEditorInput_updatesDurationAndTargetWhenRunning() {
        Countdown cd = new Countdown("t3", handler.getType(), defaults.displayTypes(), defaults.updateIntervalSeconds(), null, "", "", "", java.util.List.of(), defaults.zoneId());
        cd.setRunning(true);
        Instant now = Instant.parse("2026-01-01T00:00:00Z");
        boolean applied = handler.tryApplyEditorInput("30s", cd, now);
        assertTrue(applied);
        assertEquals(30, cd.getDurationSeconds());
        assertEquals(Instant.parse("2026-01-01T00:00:30Z"), cd.getTargetInstant());
    }
}
