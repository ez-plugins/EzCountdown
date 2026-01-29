package com.skyblockexp.ezcountdown.type;

import com.skyblockexp.ezcountdown.display.DisplayType;
import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.manager.CountdownDefaults;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

public class ManualHandlerTest {
    private final ManualHandler handler = new ManualHandler();
    private final CountdownDefaults defaults = new CountdownDefaults(EnumSet.noneOf(DisplayType.class), 1, null, "", "", "", false, ZoneId.of("UTC"));

    @Test
    public void configureFromCreateArgs_setsDurationAndNotRunning() {
        Countdown cd = new Countdown("m", handler.getType(), defaults.displayTypes(), defaults.updateIntervalSeconds(), null, "", "", "", java.util.List.of(), defaults.zoneId());
        handler.configureFromCreateArgs(cd, new String[]{"45s"}, defaults);
        assertEquals(45, cd.getDurationSeconds());
        assertFalse(cd.isRunning());
    }

    @Test
    public void onStart_setsTarget() {
        Countdown cd = new Countdown("m2", handler.getType(), defaults.displayTypes(), defaults.updateIntervalSeconds(), null, "", "", "", java.util.List.of(), defaults.zoneId());
        cd.setDurationSeconds(5);
        Instant now = Instant.parse("2026-01-01T00:00:00Z");
        handler.onStart(cd, now);
        assertEquals(Instant.parse("2026-01-01T00:00:05Z"), cd.getTargetInstant());
    }
}
