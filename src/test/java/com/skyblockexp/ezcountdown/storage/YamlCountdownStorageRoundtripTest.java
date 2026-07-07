package com.skyblockexp.ezcountdown.storage;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.display.DisplayType;
import com.skyblockexp.ezcountdown.manager.CountdownDefaults;
import com.skyblockexp.ezcountdown.type.DurationHandler;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class YamlCountdownStorageRoundtripTest {

    @Test
    public void saveAndLoadRoundtrip() throws Exception {
        File tmp = File.createTempFile("countdowns-test", ".yml");
        tmp.delete();
        tmp.getParentFile().mkdirs();

        CountdownDefaults defaults = new CountdownDefaults(EnumSet.noneOf(DisplayType.class), 1, null, "{formatted}", "start", "end", true, ZoneId.of("UTC"));
        YamlCountdownStorage storage = new YamlCountdownStorage(defaults, tmp, java.util.logging.Logger.getLogger("test"));

        Countdown d1 = new Countdown("duration1", CountdownType.DURATION, EnumSet.noneOf(DisplayType.class), 1, null, "{formatted}", "s", "e", List.of(), ZoneId.of("UTC"));
        d1.setDurationSeconds(10);
        d1.setRunning(false);

        Countdown f1 = new Countdown("fixed1", CountdownType.FIXED_DATE, EnumSet.noneOf(DisplayType.class), 1, null, "{formatted}", "s", "e", List.of(), ZoneId.of("UTC"));
        f1.setRunning(true);
        f1.setTargetInstant(Instant.now().plusSeconds(3600));

        storage.saveCountdowns(List.of(d1, f1));

        // reload from file
        YamlCountdownStorage reloader = new YamlCountdownStorage(defaults, tmp, java.util.logging.Logger.getLogger("test2"));
        var loaded = reloader.loadCountdowns();
        assertEquals(2, loaded.size());
        boolean foundDuration = loaded.stream().anyMatch(c -> c.getName().equals("duration1") && c.getType() == CountdownType.DURATION);
        boolean foundFixed = loaded.stream().anyMatch(c -> c.getName().equals("fixed1") && c.getType() == CountdownType.FIXED_DATE);
        assertTrue(foundDuration);
        assertTrue(foundFixed);
        tmp.delete();
    }

        @Test
        public void saveAndLoadRoundtrip_preservesStartAndEndSounds() throws Exception {
                File tmp = File.createTempFile("countdowns-sounds", ".yml");
                tmp.delete();
                tmp.getParentFile().mkdirs();

                CountdownDefaults defaults = new CountdownDefaults(EnumSet.noneOf(DisplayType.class), 1, null, "{formatted}", "start", "end", true, ZoneId.of("UTC"));
                YamlCountdownStorage storage = new YamlCountdownStorage(defaults, tmp, java.util.logging.Logger.getLogger("test-sounds"));

                Countdown cd = new Countdown("soundy", CountdownType.MANUAL, EnumSet.noneOf(DisplayType.class), 1, null, "{formatted}", "s", "e", List.of(), ZoneId.of("UTC"));
                cd.setStartSound("ENTITY_PLAYER_LEVELUP");
                cd.setEndSound("BLOCK_NOTE_BLOCK_PLING");

                storage.saveCountdowns(List.of(cd));

                YamlCountdownStorage reloader = new YamlCountdownStorage(defaults, tmp, java.util.logging.Logger.getLogger("test-sounds-reload"));
                var loaded = reloader.loadCountdowns();
                Countdown loadedCd = loaded.stream().filter(c -> c.getName().equals("soundy")).findFirst().orElseThrow();

                assertEquals("ENTITY_PLAYER_LEVELUP", loadedCd.getStartSound());
                assertEquals("BLOCK_NOTE_BLOCK_PLING", loadedCd.getEndSound());
                tmp.delete();
        }

    /**
     * Regression test for: duration countdown resets to full duration on server restart.
     *
     * Scenario: a duration countdown is active and displayed via scoreboard/placeholder.
     * After saving and reloading (simulating a server restart) the countdown should resume
     * from its remaining time, NOT reset to the full configured duration.
     *
     * This test exposes the bug: {@code DurationHandler.serialize()} never persists
     * {@code targetInstant}, so on reload {@code parse()} recalculates it from
     * {@code Instant.now() + fullDuration} instead of restoring the original target.
     */
    @Test
    public void durationCountdownPreservesTargetInstantAcrossRestart() throws Exception {
        File tmp = File.createTempFile("countdowns-restart-bug", ".yml");
        tmp.deleteOnExit();

        CountdownDefaults defaults = new CountdownDefaults(
                EnumSet.noneOf(DisplayType.class), 1, null,
                "{formatted}", "start", "end", true, ZoneId.of("UTC"));

        YamlCountdownStorage storage = new YamlCountdownStorage(
                defaults, tmp, java.util.logging.Logger.getLogger("restart-bug-save"));
        storage.setHandlerRegistry(Map.of(CountdownType.DURATION, new DurationHandler()));

        long fullDurationSeconds = 1000L;
        // Simulate the countdown already being halfway through: 500s remaining out of 1000s.
        long remainingSeconds = 500L;

        Countdown original = new Countdown("scoreboard-event", CountdownType.DURATION,
                EnumSet.of(DisplayType.SCOREBOARD), 1, null, "{time}", "start", "end",
                List.of(), ZoneId.of("UTC"));
        original.setDurationSeconds(fullDurationSeconds);
        original.setRunning(true);
        Instant originalTarget = Instant.now().plusSeconds(remainingSeconds);
        original.setTargetInstant(originalTarget);

        // "Server shuts down" — save state to disk.
        storage.saveCountdowns(List.of(original));

        // "Server restarts" — load state from disk with a fresh storage instance.
        YamlCountdownStorage reloader = new YamlCountdownStorage(
                defaults, tmp, java.util.logging.Logger.getLogger("restart-bug-load"));
        reloader.setHandlerRegistry(Map.of(CountdownType.DURATION, new DurationHandler()));
        Collection<Countdown> loaded = reloader.loadCountdowns();

        Countdown reloaded = loaded.stream()
                .filter(c -> c.getName().equals("scoreboard-event"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Countdown 'scoreboard-event' not found after reload"));

        assertNotNull(reloaded.getTargetInstant(),
                "targetInstant must be non-null after restart — countdown should still be running");

        // Remaining time must be close to the original 500s, not reset to the full 1000s.
        long reloadedRemainingSeconds =
                Duration.between(Instant.now(), reloaded.getTargetInstant()).toSeconds();

        assertTrue(reloadedRemainingSeconds <= remainingSeconds + 5,
                "BUG ACTIVE — countdown reset to full duration on restart. " +
                "Expected remaining ≈ " + remainingSeconds + "s but got " + reloadedRemainingSeconds + "s.");
    }
}
