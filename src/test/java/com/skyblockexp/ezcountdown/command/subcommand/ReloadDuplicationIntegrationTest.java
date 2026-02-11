package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReloadDuplicationIntegrationTest extends MockBukkitTestBase {

    @Test
    public void reloadDoesNotDuplicateRunningCountdowns() throws Exception {
        // manager is provided by MockBukkitTestBase.baseSetup
        var manager = registry.countdowns();
        // Create a simple manual countdown that is running
        var countdown = new com.skyblockexp.ezcountdown.api.model.Countdown("dup-test", com.skyblockexp.ezcountdown.api.model.CountdownType.MANUAL,
                java.util.EnumSet.of(com.skyblockexp.ezcountdown.display.DisplayType.ACTION_BAR), 1, null,
                "{formatted}", "start", "end", java.util.List.of(), java.time.ZoneId.systemDefault(), false, null, 0, false, null, com.skyblockexp.ezcountdown.api.model.MissedRunPolicy.SKIP);
        countdown.setRunning(true);
        boolean created = manager.createCountdown(countdown);
        org.junit.jupiter.api.Assertions.assertTrue(created, "createCountdown should return true");

        // Ensure one running countdown
        System.out.println("created=" + created + ", count=" + manager.getCountdownCount());
        assertEquals(1, manager.getCountdownCount());

        // Ensure storage contains the countdown so load() will preserve it
        java.io.File data = plugin.getDataFolder();
        if (!data.exists()) data.mkdirs();
        java.io.File f = new java.io.File(data, "countdowns.yml");
        org.bukkit.configuration.file.FileConfiguration cfg = new org.bukkit.configuration.file.YamlConfiguration();
        cfg.createSection("countdowns");
        cfg.set("countdowns.dup-test.type", "MANUAL");
        cfg.set("countdowns.dup-test.running", true);
        cfg.set("countdowns.dup-test.duration", "60s");
        cfg.save(f);

        // Simulate reload: stop current countdowns and reload from storage
        try { registry.countdowns().shutdown(); } catch (Exception ignored) {}
        try { registry.countdowns().load(); } catch (Exception ignored) {}

        // After reload, ensure still exactly one countdown exists (no duplication)
        assertEquals(1, manager.getCountdownCount());
    }
}
