package com.skyblockexp.ezcountdown.manager;

import com.skyblockexp.ezcountdown.bootstrap.Registry;
import com.skyblockexp.ezcountdown.storage.CountdownStorage;
import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CountdownManagerReentrantDisplayTest {

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
        when(plugin.getLogger()).thenReturn(java.util.logging.Logger.getLogger("test"));
        when(plugin.getDataFolder()).thenReturn(new java.io.File("target"));

        // Prepare a mock Bukkit server that will simulate a blocking dispatchCommand
        org.bukkit.Server bukkitServer = mock(org.bukkit.Server.class);
        org.bukkit.plugin.PluginManager pm = mock(org.bukkit.plugin.PluginManager.class);
        when(bukkitServer.getPluginManager()).thenReturn(pm);

        dispatchCount = new AtomicInteger(0);
        try {
            when(bukkitServer.dispatchCommand(any(org.bukkit.command.CommandSender.class), anyString())).thenAnswer(invocation -> {
                // Simulate a slow command execution to create overlap between concurrent ticks
                try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                dispatchCount.incrementAndGet();
                return true;
            });
        } catch (Exception e) {
            // Mockito signature compatibility
            e.printStackTrace();
        }

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

    @Test
    public void displayAllReentrantTickDoesNotDoubleExecuteEnd() throws Exception {
        Countdown c = new Countdown("multi", CountdownType.MANUAL, EnumSet.of(com.skyblockexp.ezcountdown.display.DisplayType.ACTION_BAR, com.skyblockexp.ezcountdown.display.DisplayType.SCOREBOARD), 1, null, "{formatted}", "start", "end", List.of("/say hey"), ZoneId.systemDefault());
        c.setRunning(true);
        c.setTargetInstant(java.time.Instant.now().minusSeconds(2));
        manager.createCountdown(c);

        java.lang.reflect.Method tick = CountdownManager.class.getDeclaredMethod("tick");
        tick.setAccessible(true);

        // make displayManager.displayAll call tick() reentrantly
        doAnswer(invocation -> {
            // re-enter tick (simulating a display handler triggering a scheduler)
            tick.invoke(manager);
            return null;
        }).when(displayManager).displayAll(anyCollection(), anyMap(), anyMap());

        // invoke first tick
        tick.invoke(manager);

        // after reentrant call, ensure end commands executed exactly once
        assertTrue(manager.getExecutedCount() >= 1);
        assertEquals(1, dispatchCount.get(), "Console dispatchCommand should be executed exactly once even with reentrant displayAll");
    }
}
