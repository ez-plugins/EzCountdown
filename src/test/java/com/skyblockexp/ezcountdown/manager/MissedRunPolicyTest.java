package com.skyblockexp.ezcountdown.manager;

import com.skyblockexp.ezcountdown.bootstrap.Registry;
import com.skyblockexp.ezcountdown.storage.CountdownStorage;
import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownBuilder;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MissedRunPolicyTest {

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
        // mock Bukkit server to avoid NPEs
        org.bukkit.Server bukkitServer = mock(org.bukkit.Server.class);
        org.bukkit.plugin.PluginManager pm = mock(org.bukkit.plugin.PluginManager.class);
        org.bukkit.command.ConsoleCommandSender console = mock(org.bukkit.command.ConsoleCommandSender.class);
        org.bukkit.scheduler.BukkitTask task = mock(org.bukkit.scheduler.BukkitTask.class);
        org.bukkit.scheduler.BukkitScheduler scheduler = (org.bukkit.scheduler.BukkitScheduler) java.lang.reflect.Proxy.newProxyInstance(
            org.bukkit.scheduler.BukkitScheduler.class.getClassLoader(),
            new Class[]{org.bukkit.scheduler.BukkitScheduler.class},
            (proxy, method, margs) -> {
                String m = method.getName();
                if ("runTaskTimer".equals(m) || "runTaskLater".equals(m)) return task;
                // return sensible defaults for primitive returns
                Class<?> ret = method.getReturnType();
                if (ret == boolean.class) return false;
                if (ret == int.class) return 0;
                return null;
            }
        );
        when(bukkitServer.getPluginManager()).thenReturn(pm);
        when(bukkitServer.getConsoleSender()).thenReturn(console);
        when(bukkitServer.getScheduler()).thenReturn(scheduler);
        try {
            java.lang.reflect.Field serverField = org.bukkit.Bukkit.class.getDeclaredField("server");
            serverField.setAccessible(true);
            serverField.set(null, bukkitServer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void runSingleExecutesMissedOccurrenceOnLoad() {
        // Create a recurring countdown aligned to clock with a 2h interval and RUN_SINGLE policy
        Countdown cd = CountdownBuilder.builder("missed1")
                .type(CountdownType.RECURRING)
                .zoneId(ZoneId.of("UTC"))
                .alignToClock(true)
                .alignInterval("2h")
                .missedRunPolicy(com.skyblockexp.ezcountdown.api.model.MissedRunPolicy.RUN_SINGLE)
                .build();
        cd.setRunning(true);

        when(storage.loadCountdowns()).thenReturn(List.of(cd));

        manager = new CountdownManager(registry, null, storage, displayManager, messageManager, locationManager);
        manager.load();

        assertTrue(manager.getExecutedCount() > 0, "Expected at least one missed-run to have executed");
    }

    @Test
    public void skipDoesNotExecuteMissedOccurrenceOnLoad() {
        Countdown cd = CountdownBuilder.builder("missed2")
                .type(CountdownType.RECURRING)
                .zoneId(ZoneId.of("UTC"))
                .alignToClock(true)
                .alignInterval("2h")
                .missedRunPolicy(com.skyblockexp.ezcountdown.api.model.MissedRunPolicy.SKIP)
                .build();
        cd.setRunning(true);

        when(storage.loadCountdowns()).thenReturn(List.of(cd));

        manager = new CountdownManager(registry, null, storage, displayManager, messageManager, locationManager);
        manager.load();

        assertEquals(0, manager.getExecutedCount(), "Expected no missed-run executions for SKIP policy");
        verify(displayManager, never()).broadcastMessage(anyString());
    }
}
