package com.skyblockexp.ezcountdown.command;

import com.skyblockexp.ezcountdown.api.event.CountdownEndEvent;
import com.skyblockexp.ezcountdown.api.event.CountdownStartEvent;
import com.skyblockexp.ezcountdown.api.event.CountdownTickEvent;
import com.skyblockexp.ezcountdown.bootstrap.Registry;
import com.skyblockexp.ezcountdown.manager.*;
import com.skyblockexp.ezcountdown.storage.CountdownStorage;
import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CountdownEventFeatureTest {

    private Registry registry;
    private CountdownManager manager;
    private CountdownStorage storage;
    private DisplayManager displayManager;
    private MessageManager messageManager;
    private LocationManager locationManager;
    private com.skyblockexp.ezcountdown.EzCountdownPlugin plugin;
    private PluginManager pm;

    @BeforeEach
    public void setup() throws Exception {
        registry = mock(Registry.class);
        storage = mock(CountdownStorage.class);
        displayManager = mock(DisplayManager.class);
        messageManager = mock(MessageManager.class);
        locationManager = mock(LocationManager.class);
        plugin = mock(com.skyblockexp.ezcountdown.EzCountdownPlugin.class);
        when(registry.plugin()).thenReturn(plugin);
        when(plugin.getLogger()).thenReturn(java.util.logging.Logger.getLogger("test"));
        when(plugin.getDataFolder()).thenReturn(new java.io.File("target"));

        // mock Bukkit.server and its plugin manager so callEvent works and can be verified
        org.bukkit.Server bukkitServer = mock(org.bukkit.Server.class);
        pm = mock(PluginManager.class);
        org.bukkit.command.ConsoleCommandSender console = mock(org.bukkit.command.ConsoleCommandSender.class);
        when(bukkitServer.getPluginManager()).thenReturn(pm);
        when(bukkitServer.getConsoleSender()).thenReturn(console);
        java.lang.reflect.Field serverField = org.bukkit.Bukkit.class.getDeclaredField("server");
        serverField.setAccessible(true);
        serverField.set(null, bukkitServer);

        when(registry.countdowns()).thenReturn(null); // not used by manager internals here
        manager = new CountdownManager(registry, null, storage, displayManager, messageManager, locationManager);
    }

    @Test
    public void firesStartEventWhenCreatingRunningCountdown() {
        Countdown c = new Countdown("evtstart", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "{formatted}", "start", "end", java.util.List.of(), ZoneId.systemDefault());
        c.setRunning(true);
        c.setDurationSeconds(5);

        manager.createCountdown(c);

        // verify plugin manager was asked to call a CountdownStartEvent
        verify(pm, atLeastOnce()).callEvent(argThat(ev -> ev instanceof CountdownStartEvent && ((CountdownStartEvent) ev).getCountdown().getName().equals("evtstart")));
    }

    @Test
    public void firesTickEventDuringTick() throws Exception {
        Countdown c = new Countdown("evttick", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "{formatted}", null, null, java.util.List.of(), ZoneId.systemDefault());
        c.setRunning(true);
        c.setDurationSeconds(60);
        c.setTargetInstant(Instant.now().plusSeconds(30));
        manager.createCountdown(c);

        // invoke private tick()
        java.lang.reflect.Method tick = CountdownManager.class.getDeclaredMethod("tick");
        tick.setAccessible(true);
        tick.invoke(manager);

        verify(pm, atLeastOnce()).callEvent(argThat(ev -> ev instanceof CountdownTickEvent && ((CountdownTickEvent) ev).getCountdown().getName().equals("evttick")));
    }

    @Test
    public void firesEndEventWhenCountdownExpires() throws Exception {
        Countdown c = new Countdown("evtend", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "{formatted}", null, null, java.util.List.of(), ZoneId.systemDefault());
        c.setRunning(true);
        c.setTargetInstant(Instant.now().minusSeconds(10));
        manager.createCountdown(c);

        java.lang.reflect.Method tick = CountdownManager.class.getDeclaredMethod("tick");
        tick.setAccessible(true);
        tick.invoke(manager);

        verify(pm, atLeastOnce()).callEvent(argThat(ev -> ev instanceof CountdownEndEvent && ((CountdownEndEvent) ev).getCountdown().getName().equals("evtend")));
    }
}
