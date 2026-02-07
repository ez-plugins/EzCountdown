package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class CreateSubcommandNegativeTest extends MockBukkitTestBase {

    @Test
    public void insufficientArgsDoesNotCreate() {
        CreateSubcommand sub = new CreateSubcommand(registry);
        // missing args: only 'create' provided
        sub.execute(server.getConsoleSender(), new String[]{"create"});
        assertFalse(manager.getCountdown("").isPresent());
    }

    @Test
    public void invalidDurationDoesNotCreate() {
        CreateSubcommand sub = new CreateSubcommand(registry);
        sub.execute(server.getConsoleSender(), new String[]{"create", "badcd", "duration", "not-a-duration"});
        assertFalse(manager.getCountdown("badcd").isPresent());
    }
}
