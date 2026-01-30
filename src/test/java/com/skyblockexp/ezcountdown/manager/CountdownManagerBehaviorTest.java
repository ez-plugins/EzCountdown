package com.skyblockexp.ezcountdown.manager;

import com.skyblockexp.ezcountdown.bootstrap.Registry;
import com.skyblockexp.ezcountdown.storage.CountdownStorage;
import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CountdownManagerBehaviorTest {

    private CountdownManager manager;
    private Registry registry;
    private CountdownStorage storage;
    private DisplayManager displayManager;
    private MessageManager messageManager;
    private LocationManager locationManager;
    private com.skyblockexp.ezcountdown.EzCountdownPlugin plugin;

    @BeforeEach
    public void setup() {
        registry = mock(Registry.class);
        storage = mock(CountdownStorage.class);
        displayManager = mock(DisplayManager.class);
        messageManager = mock(MessageManager.class);
        locationManager = mock(LocationManager.class);
        plugin = mock(com.skyblockexp.ezcountdown.EzCountdownPlugin.class);
        when(registry.plugin()).thenReturn(plugin);
        when(plugin.getLogger()).thenReturn(java.util.logging.Logger.getLogger("test"));
        when(plugin.getDataFolder()).thenReturn(new java.io.File("target"));
        // ensure Bukkit.server is set to a mock to avoid NPEs when firing events in tests
        org.bukkit.Server bukkitServer = mock(org.bukkit.Server.class);
        org.bukkit.plugin.PluginManager pm = mock(org.bukkit.plugin.PluginManager.class);
        org.bukkit.command.ConsoleCommandSender console = mock(org.bukkit.command.ConsoleCommandSender.class);
        when(bukkitServer.getPluginManager()).thenReturn(pm);
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

    @Test
    public void createAndSaveCountdown() {
        Countdown c = new Countdown("test1", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "{formatted}", "start", "end", java.util.List.of(), ZoneId.systemDefault());
        boolean created = manager.createCountdown(c);
        assertTrue(created);
        assertEquals(1, manager.getCountdownCount());
        assertTrue(manager.getCountdown("test1").isPresent());

        manager.save();
        verify(storage).saveCountdowns(any());
    }

    @Test
    public void startAndStopCountdownUpdatesState() {
        Countdown c = new Countdown("dur", CountdownType.DURATION, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "{formatted}", "start", "end", java.util.List.of(), ZoneId.systemDefault());
        c.setDurationSeconds(10);
        manager.createCountdown(c);

        boolean started = manager.startCountdown("dur");
        assertTrue(started);
        Countdown running = manager.getCountdown("dur").orElseThrow();
        assertTrue(running.isRunning());
        assertNotNull(running.getTargetInstant());

        boolean stopped = manager.stopCountdown("dur");
        assertTrue(stopped);
        Countdown after = manager.getCountdown("dur").orElseThrow();
        assertFalse(after.isRunning());
        assertNull(after.getTargetInstant());
        verify(displayManager).clearCountdown(any());
    }

    @Test
    public void updateAndDeleteCountdown() {
        Countdown c = new Countdown("up", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "{formatted}", "start", "end", java.util.List.of(), ZoneId.systemDefault());
        manager.createCountdown(c);

        Countdown updated = new Countdown("up", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 2, null, "fmt", "s", "e", java.util.List.of("cmd"), ZoneId.systemDefault());
        boolean replaced = manager.updateCountdown("up", updated);
        assertTrue(replaced);
        Countdown fromManager = manager.getCountdown("up").orElseThrow();
        assertEquals(2, fromManager.getUpdateIntervalSeconds());

        boolean deleted = manager.deleteCountdown("up");
        assertTrue(deleted);
        assertFalse(manager.getCountdown("up").isPresent());
        verify(displayManager).clearCountdown(any());
    }

    @Test
    public void tickProcessesExpiredCountdownAndIncrementsExecutedCount() throws Exception {
        Countdown c = new Countdown("endme", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "{formatted}", "start", "end", java.util.List.of("/say done"), ZoneId.systemDefault());
        // set running and a past target so tick considers it expired
        c.setRunning(true);
        c.setTargetInstant(java.time.Instant.now().minusSeconds(5));
        manager.createCountdown(c);

        // invoke private tick() to simulate scheduler run
        java.lang.reflect.Method tick = CountdownManager.class.getDeclaredMethod("tick");
        tick.setAccessible(true);
        tick.invoke(manager);

        // after tick, executed count should have increased and displays should have been updated
        assertTrue(manager.getExecutedCount() >= 1);
        verify(displayManager, atLeastOnce()).broadcastMessage(nullable(String.class));
        verify(displayManager, atLeastOnce()).display(any(com.skyblockexp.ezcountdown.api.model.Countdown.class), nullable(String.class), anyLong());
    }
}

