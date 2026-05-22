package com.skyblockexp.ezcountdown.manager;

import com.skyblockexp.ezcountdown.bootstrap.Registry;
import com.skyblockexp.ezcountdown.storage.CountdownStorage;
import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Regression tests for:
 * 1. resumeRunningCountdowns() reset bug — DURATION countdown's remaining time is
 *    discarded on reload because the handler's onStart() is called unconditionally,
 *    overwriting the target_epoch that was correctly restored from storage.
 * 2. End commands running more than once per countdown expiry cycle.
 */
public class CountdownManagerResumeTest {

    private CountdownManager manager;
    private Registry registry;
    private CountdownStorage storage;
    private DisplayManager displayManager;
    private MessageManager messageManager;
    private LocationManager locationManager;
    private AtomicInteger dispatchCount;

    @BeforeEach
    public void setup() {
        registry = mock(Registry.class);
        storage = mock(CountdownStorage.class);
        displayManager = mock(DisplayManager.class);
        messageManager = mock(MessageManager.class);
        locationManager = mock(LocationManager.class);

        com.skyblockexp.ezcountdown.EzCountdownPlugin plugin = mock(com.skyblockexp.ezcountdown.EzCountdownPlugin.class);
        when(registry.plugin()).thenReturn(plugin);
        when(plugin.getLogger()).thenReturn(java.util.logging.Logger.getLogger("test-resume"));
        when(plugin.getDataFolder()).thenReturn(new java.io.File("target"));

        org.bukkit.Server bukkitServer = mock(org.bukkit.Server.class);
        org.bukkit.plugin.PluginManager pm = mock(org.bukkit.plugin.PluginManager.class);
        when(bukkitServer.getPluginManager()).thenReturn(pm);

        dispatchCount = new AtomicInteger(0);
        try {
            when(bukkitServer.dispatchCommand(any(org.bukkit.command.CommandSender.class), anyString()))
                    .thenAnswer(inv -> { dispatchCount.incrementAndGet(); return true; });
        } catch (Exception ignored) {}

        org.bukkit.command.ConsoleCommandSender console = mock(org.bukkit.command.ConsoleCommandSender.class);
        when(bukkitServer.getConsoleSender()).thenReturn(console);

        try {
            java.lang.reflect.Field serverField = org.bukkit.Bukkit.class.getDeclaredField("server");
            serverField.setAccessible(true);
            serverField.set(null, bukkitServer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        manager = new CountdownManager(registry, null, storage, displayManager, messageManager, locationManager);
    }

    /**
     * Regression: resumeRunningCountdowns() was calling handler.onStart() (or the legacy
     * fallback) unconditionally, which resets targetInstant to now + fullDuration every
     * reload — discarding the remaining time saved as target_epoch.
     *
     * <p>After the fix, resumeRunningCountdowns() must NOT overwrite an already-set
     * targetInstant; it should behave like ensureTarget() (no-op when target is present).
     */
    @Test
    public void resumeRunningCountdowns_preservesRemainingTimeForDuration() throws Exception {
        long fullDuration = 60L;
        long remainingSeconds = 30L; // halfway through

        Countdown c = new Countdown("reload-test", CountdownType.DURATION,
                EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class),
                1, null, "{formatted}", "start", "end", List.of(), ZoneId.systemDefault());
        c.setDurationSeconds(fullDuration);
        c.setRunning(true);
        manager.createCountdown(c);
        // Set the target AFTER createCountdown so the legacy fallback inside createCountdown
        // (which has no registered handler) does not overwrite the value we want to test.
        Instant savedTarget = Instant.now().plusSeconds(remainingSeconds);
        c.setTargetInstant(savedTarget);

        // Simulate the reload path: after load() correctly restores the target,
        // resumeRunningCountdowns() is called and must NOT reset it.
        manager.resumeRunningCountdowns();

        Countdown after = manager.getCountdown("reload-test").orElseThrow();
        assertNotNull(after.getTargetInstant(), "targetInstant must remain non-null after resume");

        long remainingAfter = Duration.between(Instant.now(), after.getTargetInstant()).toSeconds();

        // After the fix: remaining should still be ~30s, not reset to ~60s.
        assertTrue(remainingAfter <= remainingSeconds + 3,
                "BUG: resumeRunningCountdowns reset DURATION countdown to full duration. " +
                "Expected ~" + remainingSeconds + "s remaining but got " + remainingAfter + "s.");
        assertTrue(remainingAfter >= remainingSeconds - 3,
                "Remaining time dropped unexpectedly: " + remainingAfter + "s");
    }

    /**
     * Regression: end commands must fire exactly once when a DURATION countdown expires,
     * even if tick() is called multiple times after the expiry.
     */
    @Test
    public void endCommands_fireExactlyOncePerExpiry() throws Exception {
        Countdown c = new Countdown("cmd-once", CountdownType.DURATION,
                EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class),
                1, null, "{formatted}", "start", "end",
                List.of("/say done"), ZoneId.systemDefault());
        c.setDurationSeconds(10);
        c.setRunning(true);
        manager.createCountdown(c);
        // Override the target set by createCountdown's legacy fallback so the countdown
        // appears already expired when tick() is invoked.
        c.setTargetInstant(Instant.now().minusSeconds(2));

        java.lang.reflect.Method tick = CountdownManager.class.getDeclaredMethod("tick");
        tick.setAccessible(true);

        // First tick — end should be handled, commands dispatched once.
        tick.invoke(manager);
        assertEquals(1, dispatchCount.get(), "command should be dispatched exactly once on first tick");

        // Second tick — countdown is no longer running; commands must NOT fire again.
        tick.invoke(manager);
        assertEquals(1, dispatchCount.get(), "command must NOT be dispatched a second time on subsequent tick");
    }

    /**
     * Regression: the end-message broadcast must be sent exactly once per countdown
     * expiry, even if tick() is called multiple times after the expiry.
     */
    @Test
    public void endMessage_broadcastedExactlyOncePerExpiry() throws Exception {
        Countdown c = new Countdown("msg-once", CountdownType.DURATION,
                EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class),
                1, null, "{formatted}", "start-msg", "end-msg",
                List.of(), ZoneId.systemDefault());
        c.setDurationSeconds(10);
        c.setRunning(true);
        manager.createCountdown(c);
        // Override target so the countdown is already expired when tick() runs.
        c.setTargetInstant(Instant.now().minusSeconds(2));

        // Clear call history accumulated during createCountdown (e.g., fireStart broadcast).
        reset(displayManager);

        java.lang.reflect.Method tick = CountdownManager.class.getDeclaredMethod("tick");
        tick.setAccessible(true);

        // First tick — end must be handled and message broadcast exactly once.
        tick.invoke(manager);
        verify(displayManager, times(1)).broadcastMessage(any());

        // Second tick — countdown is stopped; message must NOT be broadcast again.
        tick.invoke(manager);
        verify(displayManager, times(1)).broadcastMessage(any()); // still 1
    }

    /**
     * Regression: after a simulated reload (resumeRunningCountdowns() + tick()),
     * commands for a DURATION countdown that had already ended must NOT fire again.
     */
    @Test
    public void endCommands_doNotFireAgainAfterReload() throws Exception {
        Countdown c = new Countdown("cmd-reload", CountdownType.DURATION,
                EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class),
                1, null, "{formatted}", "start", "end",
                List.of("/say finished"), ZoneId.systemDefault());
        c.setDurationSeconds(10);
        c.setRunning(false); // countdown already ended, saved state
        c.setTargetInstant(null);
        manager.createCountdown(c);

        // Simulate reload: resumeRunningCountdowns skips non-running countdowns
        manager.resumeRunningCountdowns();

        java.lang.reflect.Method tick = CountdownManager.class.getDeclaredMethod("tick");
        tick.setAccessible(true);
        tick.invoke(manager);

        assertEquals(0, dispatchCount.get(),
                "commands must not fire for a countdown that is not running after reload");
    }
}
