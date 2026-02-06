package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FixedDatePositiveTest extends MockBukkitTestBase {

    @Test
    public void createFixedDateSetsTarget() {
        CreateSubcommand sub = new CreateSubcommand(registry);
        // create with date and time tokens
        sub.execute(server.getConsoleSender(), new String[]{"create", "fdate", "2026-03-01", "12:00"});

        var opt = manager.getCountdown("fdate");
        assertTrue(opt.isPresent(), "Fixed-date countdown should be created");
        var cd = opt.get();
        assertNotNull(cd.getTargetInstant(), "Fixed-date countdown must have a target instant");
        // If defaults say start on create, it should be running
        if (registry.defaults().startOnCreate()) assertTrue(cd.isRunning());
    }
}
