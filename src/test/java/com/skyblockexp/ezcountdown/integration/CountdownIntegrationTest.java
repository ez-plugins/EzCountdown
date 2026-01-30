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

public class CountdownIntegrationTest extends MockBukkitTestBase {

    @Test
    public void startEventIsDeliveredToListener() {
        AtomicBoolean seen = new AtomicBoolean(false);
        plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onStart(CountdownStartEvent e) {
                if ("itest-start".equals(e.getCountdown().getName())) seen.set(true);
            }
        }, plugin);

        Countdown c = new Countdown("itest-start", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "{formatted}", "start", "end", java.util.List.of(), ZoneId.systemDefault());
        c.setRunning(true);
        manager.createCountdown(c);

        assertTrue(seen.get(), "CountdownStartEvent listener did not receive event");
    }

    @Test
    public void tickEventIsDeliveredToListener() throws Exception {
        AtomicBoolean seen = new AtomicBoolean(false);
        plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onTick(CountdownTickEvent e) {
                if ("itest-tick".equals(e.getCountdown().getName())) seen.set(true);
            }
        }, plugin);

        Countdown c = new Countdown("itest-tick", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "{formatted}", null, null, java.util.List.of(), ZoneId.systemDefault());
        c.setRunning(true);
        c.setDurationSeconds(60);
        c.setTargetInstant(Instant.now().plusSeconds(30));
        manager.createCountdown(c);

        java.lang.reflect.Method tick = CountdownManager.class.getDeclaredMethod("tick");
        tick.setAccessible(true);
        tick.invoke(manager);

        assertTrue(seen.get(), "CountdownTickEvent listener did not receive event");
    }

    @Test
    public void endEventIsDeliveredToListener() throws Exception {
        AtomicBoolean seen = new AtomicBoolean(false);
        plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onEnd(CountdownEndEvent e) {
                if ("itest-end".equals(e.getCountdown().getName())) seen.set(true);
            }
        }, plugin);

        Countdown c = new Countdown("itest-end", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "{formatted}", null, null, java.util.List.of(), ZoneId.systemDefault());
        c.setRunning(true);
        c.setTargetInstant(Instant.now().minusSeconds(5));
        manager.createCountdown(c);

        java.lang.reflect.Method tick = CountdownManager.class.getDeclaredMethod("tick");
        tick.setAccessible(true);
        tick.invoke(manager);

        assertTrue(seen.get(), "CountdownEndEvent listener did not receive event");
    }
}
