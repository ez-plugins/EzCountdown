package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

public class StopSubcommandTest extends MockBukkitTestBase {

    @Test
    public void execute_stopsCountdown() {
        Countdown cd = new Countdown("sstop", CountdownType.MANUAL,
                EnumSet.copyOf(registry.defaults().displayTypes()), registry.defaults().updateIntervalSeconds(),
                registry.defaults().visibilityPermission(), registry.defaults().formatMessage(), registry.defaults().startMessage(), registry.defaults().endMessage(), java.util.List.of(), registry.defaults().zoneId());
        cd.setRunning(true);
        manager.createCountdown(cd);

        StopSubcommand sub = new StopSubcommand(registry);
        sub.execute(server.getConsoleSender(), new String[]{"stop", "sstop"});

        var opt = manager.getCountdown("sstop");
        assertTrue(opt.isPresent(), "Countdown should still exist after stopping");
        assertFalse(opt.get().isRunning(), "Countdown should not be running after stop");
    }
}
