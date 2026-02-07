package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.display.DisplayType;
import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

public class StartSubcommandTest extends MockBukkitTestBase {

    @Test
    public void execute_startsCountdown() {
        com.skyblockexp.ezcountdown.manager.MessageManager spy = spyMessages();

        Countdown initial = new Countdown("sstart", CountdownType.DURATION,
                EnumSet.copyOf(registry.defaults().displayTypes()), registry.defaults().updateIntervalSeconds(),
                registry.defaults().visibilityPermission(), registry.defaults().formatMessage(), registry.defaults().startMessage(), registry.defaults().endMessage(), java.util.List.of(), registry.defaults().zoneId());
        initial.setDurationSeconds(30);
        manager.createCountdown(initial);

        StartSubcommand sub = new StartSubcommand(registry);
        sub.execute(server.getConsoleSender(), new String[]{"start", "sstart"});

        var opt = manager.getCountdown("sstart");
        assertTrue(opt.isPresent(), "Countdown should exist after start command");
        var cd2 = opt.get();
        assertTrue(cd2.isRunning(), "Countdown should be running after start");
        assertNotNull(cd2.getTargetInstant(), "Started countdown must have a target instant set by handler/onStart");
        org.mockito.Mockito.verify(spy).message("commands.start.success", java.util.Map.of("name", "sstart"));
    }
}
