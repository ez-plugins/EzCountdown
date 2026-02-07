package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class ReloadIntegrationTest extends MockBukkitTestBase {

    @Test
    public void reloadReplacesCountdownsAndRespectsRunningFlags() throws Exception {
        File data = plugin.getDataFolder();
        if (!data.exists()) data.mkdirs();

        // Create initial countdowns that should be removed/edited by reload
        com.skyblockexp.ezcountdown.api.model.Countdown old = new com.skyblockexp.ezcountdown.api.model.Countdown("old-countdown", com.skyblockexp.ezcountdown.api.model.CountdownType.MANUAL,
                java.util.EnumSet.of(com.skyblockexp.ezcountdown.display.DisplayType.CHAT), 1, null, "{formatted}", "start", "end",
                java.util.List.of(), java.time.ZoneId.systemDefault());
        old.setRunning(true);
        old.setDurationSeconds(60);
        manager.createCountdown(old);

        com.skyblockexp.ezcountdown.api.model.Countdown edited = new com.skyblockexp.ezcountdown.api.model.Countdown("edited-countdown", com.skyblockexp.ezcountdown.api.model.CountdownType.MANUAL,
                java.util.EnumSet.of(com.skyblockexp.ezcountdown.display.DisplayType.CHAT), 1, null, "{formatted}", "start", "end",
                java.util.List.of(), java.time.ZoneId.systemDefault());
        edited.setRunning(true);
        edited.setDurationSeconds(30);
        manager.createCountdown(edited);

        // Prepare new countdowns.yml that removes 'old-countdown', edits 'edited-countdown', and adds 'new-countdown'
        File f = new File(data, "countdowns.yml");
        org.bukkit.configuration.file.FileConfiguration cfg = new org.bukkit.configuration.file.YamlConfiguration();
        cfg.createSection("countdowns");

        // edited-countdown: change duration and set running=false
        cfg.set("countdowns.edited-countdown.type", "MANUAL");
        cfg.set("countdowns.edited-countdown.running", false);
        cfg.set("countdowns.edited-countdown.duration", "120s");

        // new-countdown: new entry that should be running
        cfg.set("countdowns.new-countdown.type", "MANUAL");
        cfg.set("countdowns.new-countdown.running", true);
        cfg.set("countdowns.new-countdown.duration", "10s");

        cfg.save(f);

        // Trigger reload via command to exercise the real reload path
        server.dispatchCommand(server.getConsoleSender(), "ezcd reload");

        // Let scheduler start tasks and ensure manager loaded new data
        server.getScheduler().performTicks(2);

        // old-countdown should be removed
        assertFalse(manager.getCountdown("old-countdown").isPresent(), "old-countdown should be removed after reload");

        // edited-countdown should be present with updated duration and not running
        var maybeEdited = manager.getCountdown("edited-countdown");
        assertTrue(maybeEdited.isPresent(), "edited-countdown should exist after reload");
        com.skyblockexp.ezcountdown.api.model.Countdown editedAfter = maybeEdited.get();
        assertEquals(120L, editedAfter.getDurationSeconds(), "edited-countdown duration should reflect YAML change");
        assertFalse(editedAfter.isRunning(), "edited-countdown running flag should be false after reload");

        // new-countdown should be present and running
        var maybeNew = manager.getCountdown("new-countdown");
        assertTrue(maybeNew.isPresent(), "new-countdown should exist after reload");
        com.skyblockexp.ezcountdown.api.model.Countdown newAfter = maybeNew.get();
        assertTrue(newAfter.isRunning(), "new-countdown should be running after reload");
        assertNotNull(newAfter.getTargetInstant(), "running manual countdown should have a target instant after load");

        // Invoke private tick() to exercise runtime behavior deterministically
        java.lang.reflect.Method tick = com.skyblockexp.ezcountdown.manager.CountdownManager.class.getDeclaredMethod("tick");
        tick.setAccessible(true);
        tick.invoke(manager);

        // After a tick, ensure displays/target handling didn't stop the running countdown unexpectedly
        assertTrue(newAfter.isRunning(), "new-countdown should still be running after tick (target in future)");
    }
}

