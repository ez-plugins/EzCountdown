package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class InfoSubcommandTest extends MockBukkitTestBase {

    @Test
    public void execute_showsInfoWithoutError() {
        com.skyblockexp.ezcountdown.manager.MessageManager spy = spyMessages();

        Countdown cd = new Countdown("info1", CountdownType.MANUAL,
            EnumSet.copyOf(registry.defaults().displayTypes()), registry.defaults().updateIntervalSeconds(),
            registry.defaults().visibilityPermission(), registry.defaults().formatMessage(), registry.defaults().startMessage(), registry.defaults().endMessage(), java.util.List.of(), registry.defaults().zoneId());
        manager.createCountdown(cd);

        InfoSubcommand sub = new InfoSubcommand(registry);
        sub.execute(server.getConsoleSender(), new String[]{"info", "info1"});

        org.mockito.Mockito.verify(spy).message(org.mockito.Mockito.eq("commands.info.header"), org.mockito.Mockito.anyMap());
    }
}
