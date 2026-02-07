package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class DeleteSubcommandTest extends MockBukkitTestBase {

    @Test
    public void execute_deletesCountdown() {
        com.skyblockexp.ezcountdown.manager.MessageManager spy = spyMessages();

        Countdown cd = new Countdown("sdel", CountdownType.MANUAL,
            EnumSet.copyOf(registry.defaults().displayTypes()), registry.defaults().updateIntervalSeconds(),
            registry.defaults().visibilityPermission(), registry.defaults().formatMessage(), registry.defaults().startMessage(), registry.defaults().endMessage(), java.util.List.of(), registry.defaults().zoneId());
        manager.createCountdown(cd);

        DeleteSubcommand sub = new DeleteSubcommand(registry);
        sub.execute(server.getConsoleSender(), new String[]{"delete", "sdel"});

        assertFalse(manager.getCountdown("sdel").isPresent());
        org.mockito.Mockito.verify(spy).message("commands.delete.success", java.util.Map.of("name", "sdel"));
    }
}
