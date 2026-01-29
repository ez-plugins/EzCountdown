package com.skyblockexp.ezcountdown.type;

import com.skyblockexp.ezcountdown.display.DisplayType;
import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.manager.CountdownDefaults;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

public class RecurringHandlerTest {
    private final RecurringHandler handler = new RecurringHandler();
    private final CountdownDefaults defaults = new CountdownDefaults(EnumSet.noneOf(DisplayType.class), 1, null, "", "", "", true, ZoneId.of("UTC"));

    @Test
    public void configureFromCreateArgs_setsRecurringFieldsAndTarget() {
        Countdown cd = new Countdown("r", handler.getType(), defaults.displayTypes(), defaults.updateIntervalSeconds(), null, "", "", "", java.util.List.of(), defaults.zoneId());
        handler.configureFromCreateArgs(cd, new String[]{"12","25","12:00"}, defaults);
        assertEquals(12, cd.getRecurringMonth());
        assertEquals(25, cd.getRecurringDay());
        assertNotNull(cd.getRecurringTime());
        assertTrue(cd.isRunning());
        assertNotNull(cd.getTargetInstant());
        assertTrue(cd.getTargetInstant().isAfter(Instant.now()));
    }
}
