package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RecurringSubcommandPositiveTest extends MockBukkitTestBase {

    @Test
    public void createRecurringConfiguresFields() {
        CreateSubcommand sub = new CreateSubcommand(registry);
        // month day time
        sub.execute(server.getConsoleSender(), new String[]{"create", "rgood", "recurring", "12", "25", "09:30"});

        var opt = manager.getCountdown("rgood");
        assertTrue(opt.isPresent(), "Recurring countdown should be created");
        var cd = opt.get();
        assertTrue(cd.isRunning(), "Recurring countdown should be running after creation");
        assertEquals(12, cd.getRecurringMonth());
        assertEquals(25, cd.getRecurringDay());
        assertNotNull(cd.getRecurringTime());
        assertNotNull(cd.getTargetInstant());
    }
}
