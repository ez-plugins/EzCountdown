package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReloadSubcommandFeatureTest extends MockBukkitTestBase {

    @Test
    public void reloadCommandDoesNotDuplicateRunningCountdowns() throws Exception {
        var manager = registry.countdowns();
        var countdown = new com.skyblockexp.ezcountdown.api.model.Countdown("feature-dup", com.skyblockexp.ezcountdown.api.model.CountdownType.MANUAL,
                java.util.EnumSet.of(com.skyblockexp.ezcountdown.display.DisplayType.ACTION_BAR), 1, null,
                "{formatted}", "start", "end", java.util.List.of(), java.time.ZoneId.systemDefault(), false, null, 0, false, null, com.skyblockexp.ezcountdown.api.model.MissedRunPolicy.SKIP);
        countdown.setRunning(true);
        boolean created = manager.createCountdown(countdown);
        org.junit.jupiter.api.Assertions.assertTrue(created);
        // Persist so reload will restore it from storage (tests should not rely on implicit save behavior)
        manager.save();
        // verify created and running
        assertEquals(1, manager.getCountdownCount());
        org.junit.jupiter.api.Assertions.assertTrue(manager.getCountdown("feature-dup").isPresent());
        org.junit.jupiter.api.Assertions.assertTrue(manager.getCountdown("feature-dup").get().isRunning());

        // Execute the reload subcommand as console
        boolean dispatched = server.dispatchCommand(server.getConsoleSender(), "countdown reload");
        org.junit.jupiter.api.Assertions.assertTrue(dispatched, "command dispatched");

        // Debug: print running countdowns before and after reload
        System.out.println("Running before reload: " + manager.getCountdowns().stream().filter(c -> c.isRunning()).map(com.skyblockexp.ezcountdown.api.model.Countdown::getName).toList());
        // After reload, ensure still exactly one countdown exists and it's the same running countdown
        System.out.println("Running after reload: " + manager.getCountdowns().stream().filter(c -> c.isRunning()).map(com.skyblockexp.ezcountdown.api.model.Countdown::getName).toList());
        assertEquals(1, manager.getCountdownCount());
        org.junit.jupiter.api.Assertions.assertTrue(manager.getCountdown("feature-dup").isPresent());
        org.junit.jupiter.api.Assertions.assertTrue(manager.getCountdown("feature-dup").get().isRunning());
    }
}
