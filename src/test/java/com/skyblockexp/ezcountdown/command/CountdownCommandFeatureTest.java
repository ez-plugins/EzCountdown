package com.skyblockexp.ezcountdown.command;

import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CountdownCommandFeatureTest extends MockBukkitTestBase {

    @Test
    public void createDurationUsesFallbackAndPersists() {
        boolean handled = server.dispatchCommand(server.getConsoleSender(), "ezcd create mycount duration 10s");
        assertTrue(handled, "Command was not handled");
        var opt = manager.getCountdown("mycount");
        assertTrue(opt.isPresent(), "Countdown was not created");
        assertEquals(10L, opt.get().getDurationSeconds());
    }

    @Test
    public void tabCompleteTopLevelAndStartSuggestions() {
        var command = plugin.getCommand("countdown");
        assertNotNull(command, "countdown command missing");
        var completer = command.getTabCompleter();
        assertNotNull(completer, "TabCompleter not registered");

        var top = completer.onTabComplete(server.getConsoleSender(), command, "countdown", new String[]{""});
        assertTrue(top.contains("create"));
        assertTrue(top.contains("start"));

        server.dispatchCommand(server.getConsoleSender(), "ezcd create Alpha manual 5m");
        server.dispatchCommand(server.getConsoleSender(), "ezcd create Beta manual 5m");

        var suggestions = completer.onTabComplete(server.getConsoleSender(), command, "countdown", new String[]{"start", ""});
        assertTrue(suggestions.contains("Alpha"));
        assertTrue(suggestions.contains("Beta"));
    }
}
