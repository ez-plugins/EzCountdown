package com.skyblockexp.ezcountdown.command;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;

public class CountdownCommandMockBukkitTest extends MockBukkitTestBase {

    @Test
    public void createCommandCreatesCountdown() {
        boolean dispatched = server.dispatchCommand(server.getConsoleSender(), "ezcd create itest-cmd duration 60s");
        assertTrue(dispatched, "Command was not dispatched/handled");
        assertTrue(manager.getCountdown("itest-cmd").isPresent(), "Countdown created by command was not present in manager");
    }
}
