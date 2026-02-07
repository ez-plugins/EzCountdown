package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreateSubcommandTest extends MockBukkitTestBase {

    @Test
    public void execute_createsCountdown() {
        com.skyblockexp.ezcountdown.manager.MessageManager spy = spyMessages();

        CreateSubcommand sub = new CreateSubcommand(registry);
        CommandSender sender = server.getConsoleSender();

        sub.execute(sender, new String[]{"create", "utest-countdown", "duration", "60s"});

        var opt = manager.getCountdown("utest-countdown");
        assertTrue(opt.isPresent(), "Countdown should be created by CreateSubcommand");
        var cd = opt.get();
        assertEquals(60L, cd.getDurationSeconds(), "Duration should be parsed to 60 seconds");
        if (registry.defaults().startOnCreate()) {
            assertTrue(cd.isRunning(), "Countdown should be running when start-on-create is enabled");
            assertNotNull(cd.getTargetInstant(), "Running countdown must have a target instant");
        }
        org.mockito.Mockito.verify(spy).message("commands.create.success", java.util.Map.of("name", "utest-countdown"));
    }
}
