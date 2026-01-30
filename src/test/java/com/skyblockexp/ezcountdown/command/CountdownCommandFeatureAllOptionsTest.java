package com.skyblockexp.ezcountdown.command;

import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CountdownCommandFeatureAllOptionsTest extends MockBukkitTestBase {

    @Test
    public void createAllTypes_start_stop_list_info_reload() {
        assertTrue(server.dispatchCommand(server.getConsoleSender(), "ezcd create m1 manual 5m"));
        assertTrue(manager.getCountdown("m1").isPresent());

        assertTrue(server.dispatchCommand(server.getConsoleSender(), "ezcd create d1 duration 10s"));
        assertTrue(manager.getCountdown("d1").isPresent());

        assertTrue(server.dispatchCommand(server.getConsoleSender(), "ezcd create f1 2026-02-02 12:00"));
        assertTrue(manager.getCountdown("f1").isPresent());

        assertTrue(server.dispatchCommand(server.getConsoleSender(), "ezcd create rc recurring 2 3 12:00"));
        assertTrue(manager.getCountdown("rc").isPresent());

        assertTrue(server.dispatchCommand(server.getConsoleSender(), "ezcd start d1"));
        assertTrue(manager.getCountdown("d1").get().isRunning());

        assertTrue(server.dispatchCommand(server.getConsoleSender(), "ezcd stop missing"));

        assertTrue(server.dispatchCommand(server.getConsoleSender(), "ezcd list"));

        assertTrue(server.dispatchCommand(server.getConsoleSender(), "ezcd info m1"));

        assertTrue(server.dispatchCommand(server.getConsoleSender(), "ezcd reload"));
    }

}
