package com.skyblockexp.ezcountdown.command;

import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CountdownCommandFeatureStartDeleteTest extends MockBukkitTestBase {

    @Test
    public void startCommandStartsCountdown() {
        server.dispatchCommand(server.getConsoleSender(), "ezcd create mycount duration 5m");
        assertTrue(manager.getCountdown("mycount").isPresent());
        assertTrue(server.dispatchCommand(server.getConsoleSender(), "ezcd start mycount"));
        assertTrue(manager.getCountdown("mycount").get().isRunning());
    }

    @Test
    public void deleteCommandRemovesCountdownWhenExists() {
        server.dispatchCommand(server.getConsoleSender(), "ezcd create exists manual 5m");
        assertTrue(manager.getCountdown("exists").isPresent());
        assertTrue(server.dispatchCommand(server.getConsoleSender(), "ezcd delete exists"));
        assertFalse(manager.getCountdown("exists").isPresent());
        assertTrue(server.dispatchCommand(server.getConsoleSender(), "ezcd delete nope"));
    }
}
