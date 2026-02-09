package com.skyblockexp.ezcountdown.manager;

import com.skyblockexp.ezcountdown.bootstrap.Registry;
import com.skyblockexp.ezcountdown.storage.CountdownStorage;
import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MissedRunPolicyRunAllEdgeCasesTest {

    private Registry registry;
    private CountdownStorage storage;
    private DisplayManager displayManager;
    private MessageManager messageManager;
    private LocationManager locationManager;
    private com.skyblockexp.ezcountdown.EzCountdownPlugin plugin;

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

        org.bukkit.Server bukkitServer = mock(org.bukkit.Server.class);
        org.bukkit.plugin.PluginManager pm = mock(org.bukkit.plugin.PluginManager.class);
        org.bukkit.command.ConsoleCommandSender console = mock(org.bukkit.command.ConsoleCommandSender.class);
        when(bukkitServer.getPluginManager()).thenReturn(pm);
        when(bukkitServer.getConsoleSender()).thenReturn(console);
        org.bukkit.scheduler.BukkitTask task = mock(org.bukkit.scheduler.BukkitTask.class);
        org.bukkit.scheduler.BukkitScheduler scheduler = (org.bukkit.scheduler.BukkitScheduler) java.lang.reflect.Proxy.newProxyInstance(
                org.bukkit.scheduler.BukkitScheduler.class.getClassLoader(),
                new Class[]{org.bukkit.scheduler.BukkitScheduler.class},
                (proxy, method, margs) -> {
                    String m = method.getName();
                    if ("runTaskTimer".equals(m) || "runTaskLater".equals(m)) return task;
                    Class<?> ret = method.getReturnType();
                    if (ret == boolean.class) return false;
                    if (ret == int.class) return 0;
                    return null;
                }
        );
        when(bukkitServer.getScheduler()).thenReturn(scheduler);
        java.lang.reflect.Field serverField = org.bukkit.Bukkit.class.getDeclaredField("server");
        serverField.setAccessible(true);
        serverField.set(null, bukkitServer);
    }

    @Test
    public void runAllExecutesWhenPreviousWithinWeek() {
        // Mock Countdown to control resolveNextRecurringTarget behavior
        Countdown cd = mock(Countdown.class);
        when(cd.getName()).thenReturn("runall1");
        when(cd.getType()).thenReturn(CountdownType.RECURRING);
        when(cd.isAlignToClock()).thenReturn(true);
        when(cd.getAlignInterval()).thenReturn("1d");
        when(cd.getMissedRunPolicy()).thenReturn(com.skyblockexp.ezcountdown.api.model.MissedRunPolicy.RUN_ALL);
        when(cd.isRunning()).thenReturn(true);
        // Make next occurrence 1 hour from now -> previous = now -23h which is within a week
        Instant now = Instant.now();
        when(cd.resolveNextRecurringTarget(any())).thenReturn(now.plusSeconds(3600));

        when(storage.loadCountdowns()).thenReturn(List.of(cd));

        CountdownManager manager = new CountdownManager(registry, null, storage, displayManager, messageManager, locationManager);
        manager.load();

        assertTrue(manager.getExecutedCount() > 0, "Expected RUN_ALL to execute missed occurrence when previous within week");
    }

    @Test
    public void longIntervalDoesNotExecuteWhenPreviousBeforeWeek() {
        Countdown cd = mock(Countdown.class);
        when(cd.getName()).thenReturn("runall2");
        when(cd.getType()).thenReturn(CountdownType.RECURRING);
        when(cd.isAlignToClock()).thenReturn(true);
        when(cd.getAlignInterval()).thenReturn("8d");
        when(cd.getMissedRunPolicy()).thenReturn(com.skyblockexp.ezcountdown.api.model.MissedRunPolicy.RUN_ALL);
        when(cd.isRunning()).thenReturn(true);
        // Make next occurrence 1 day from now; interval is 8 days -> previous = now -7d (equal to weekAgo) -> should NOT execute
        Instant now = Instant.now();
        when(cd.resolveNextRecurringTarget(any())).thenReturn(now.plusSeconds(24 * 3600));

        when(storage.loadCountdowns()).thenReturn(List.of(cd));

        CountdownManager manager = new CountdownManager(registry, null, storage, displayManager, messageManager, locationManager);
        manager.load();

        assertEquals(0, manager.getExecutedCount(), "Expected no executions for intervals longer than one week window");
    }

    @Test
    public void dstTransitionDailyAlignmentEuropeLondon() {
        // Test resolveNextRecurringTarget directly for DST forward day
        Countdown real = com.skyblockexp.ezcountdown.api.model.CountdownBuilder.builder("dst")
                .type(com.skyblockexp.ezcountdown.api.model.CountdownType.RECURRING)
                .alignToClock(true)
                .alignInterval("1d")
                .zoneId(ZoneId.of("Europe/London"))
                .build();

        // Choose a timestamp just before DST forward transition (example: 2026-03-29 00:30 Europe/London)
        ZonedDateTime sample = ZonedDateTime.of(2026, 3, 29, 0, 30, 0, 0, ZoneId.of("Europe/London"));
        Instant next = real.resolveNextRecurringTarget(sample.toInstant());
        // Expect next occurrence to be next local midnight
        ZonedDateTime actualLocal = ZonedDateTime.ofInstant(next, ZoneId.of("Europe/London"));
        assertEquals(sample.toLocalDate().plusDays(1), actualLocal.toLocalDate(), "Next occurrence should be next local date");
    }
}
