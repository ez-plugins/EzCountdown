package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class RecurringSubcommandNegativeTest extends MockBukkitTestBase {

    @Test
    public void missingArgsDoesNotCreate() {
        CreateSubcommand sub = new CreateSubcommand(registry);
        sub.execute(server.getConsoleSender(), new String[]{"create", "rmiss", "recurring"});
        assertFalse(manager.getCountdown("rmiss").isPresent());
    }

    @Test
    public void invalidNumberArgsDoesNotCreate() {
        CreateSubcommand sub = new CreateSubcommand(registry);
        sub.execute(server.getConsoleSender(), new String[]{"create", "rbad", "recurring", "x", "y", "12:00"});
        assertFalse(manager.getCountdown("rbad").isPresent());
    }
}
