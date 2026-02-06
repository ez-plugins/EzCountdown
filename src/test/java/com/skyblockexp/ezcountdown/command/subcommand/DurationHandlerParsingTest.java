package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DurationHandlerParsingTest extends MockBukkitTestBase {

    @Test
    public void numericSecondsAndSuffixParsed() {
        CreateSubcommand sub = new CreateSubcommand(registry);

        // numeric without suffix
        sub.execute(server.getConsoleSender(), new String[]{"create", "dnum", "duration", "60"});
        assertEquals(60L, manager.getCountdown("dnum").get().getDurationSeconds());

        // with 's' suffix
        sub.execute(server.getConsoleSender(), new String[]{"create", "dsuf", "duration", "75s"});
        assertEquals(75L, manager.getCountdown("dsuf").get().getDurationSeconds());
    }
}
