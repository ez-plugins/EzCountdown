package com.skyblockexp.ezcountdown.type;

import com.skyblockexp.ezcountdown.display.DisplayType;
import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.manager.CountdownDefaults;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

public class FixedDateHandlerTest {
    private final FixedDateHandler handler = new FixedDateHandler();
    private final CountdownDefaults defaults = new CountdownDefaults(EnumSet.noneOf(DisplayType.class), 1, null, "", "", "", true, ZoneId.of("UTC"));

    @Test
    public void configureFromCreateArgs_setsTargetInstant() {
        Countdown cd = new Countdown("f", handler.getType(), defaults.displayTypes(), defaults.updateIntervalSeconds(), null, "", "", "", java.util.List.of(), defaults.zoneId());
        handler.configureFromCreateArgs(cd, new String[]{"2026-02-01", "12:00"}, defaults);
        assertNotNull(cd.getTargetInstant());
        assertEquals(Instant.parse("2026-02-01T12:00:00Z"), cd.getTargetInstant());
    }

    @Test
    public void tryApplyEditorInput_acceptsFormattedDateTime() {
        Countdown cd = new Countdown("f2", handler.getType(), defaults.displayTypes(), defaults.updateIntervalSeconds(), null, "", "", "", java.util.List.of(), defaults.zoneId());
        boolean ok = handler.tryApplyEditorInput("2026-03-05 08:30", cd, Instant.now());
        assertTrue(ok);
        assertNotNull(cd.getTargetInstant());
    }
}
