package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

public class TabCompleteSubcommandTest extends MockBukkitTestBase {

    @Test
    public void startTabCompletesNames() {
        Countdown a = new Countdown("Alpha", CountdownType.MANUAL,
                EnumSet.copyOf(registry.defaults().displayTypes()), registry.defaults().updateIntervalSeconds(),
                registry.defaults().visibilityPermission(), registry.defaults().formatMessage(), registry.defaults().startMessage(), registry.defaults().endMessage(), java.util.List.of(), registry.defaults().zoneId());
        Countdown b = new Countdown("Beta", CountdownType.MANUAL,
                EnumSet.copyOf(registry.defaults().displayTypes()), registry.defaults().updateIntervalSeconds(),
                registry.defaults().visibilityPermission(), registry.defaults().formatMessage(), registry.defaults().startMessage(), registry.defaults().endMessage(), java.util.List.of(), registry.defaults().zoneId());
        manager.createCountdown(a);
        manager.createCountdown(b);

        StartSubcommand sub = new StartSubcommand(registry);
        var suggestions = sub.tabComplete(server.getConsoleSender(), new String[]{"start", "A"});
        assertTrue(suggestions.contains("Alpha"));
        assertFalse(suggestions.contains("Beta"));
    }
}
