package com.skyblockexp.ezcountdown.integration;

import com.skyblockexp.ezcountdown.api.event.CountdownEndEvent;
import com.skyblockexp.ezcountdown.api.event.CountdownStartEvent;
import com.skyblockexp.ezcountdown.api.event.CountdownTickEvent;
import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CountdownLifecycleFeatureTest extends MockBukkitTestBase {

    @Test
    public void lifecycleEmitsStartTickAndEndEvents() throws Exception {
        AtomicBoolean sawStart = new AtomicBoolean(false);
        AtomicBoolean sawTick = new AtomicBoolean(false);
        AtomicBoolean sawEnd = new AtomicBoolean(false);

        plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onStart(CountdownStartEvent e) { if ("lc-test".equals(e.getCountdown().getName())) sawStart.set(true); }
            @EventHandler
            public void onTick(CountdownTickEvent e) { if ("lc-test".equals(e.getCountdown().getName())) sawTick.set(true); }
            @EventHandler
            public void onEnd(CountdownEndEvent e) { if ("lc-test".equals(e.getCountdown().getName())) sawEnd.set(true); }
        }, plugin);

        Countdown c = new Countdown("lc-test", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "{fmt}", null, null, java.util.List.of(), ZoneId.systemDefault());
        // Set up duration/target so tick and end will be reachable
        c.setDurationSeconds(5);
        c.setTargetInstant(Instant.now().plusSeconds(2));
        c.setRunning(true);

        manager.createCountdown(c);

        // createCountdown with running=true should have emitted start
        assertTrue(sawStart.get(), "Start event not delivered");

        // invoke tick to cause a tick event
        java.lang.reflect.Method tick = CountdownManager.class.getDeclaredMethod("tick");
        tick.setAccessible(true);
        tick.invoke(manager);
        assertTrue(sawTick.get(), "Tick event not delivered");

        // advance time by forcing target in the past and invoke tick to trigger end
        c.setTargetInstant(Instant.now().minusSeconds(1));
        tick.invoke(manager);
        assertTrue(sawEnd.get(), "End event not delivered");
    }
}
