package com.skyblockexp.ezcountdown.integration;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownBuilder;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CountdownCoreFlowFeatureTest extends MockBukkitTestBase {

    @Test
    void autoRestartRestartsCountdownAfterNaturalEnd() throws Exception {
        Countdown looping = CountdownBuilder.builder("looping")
                .type(CountdownType.DURATION)
                .durationSeconds(20)
                .formatMessage("{formatted}")
                .startMessage("")
                .endMessage("")
                .autoRestart(true)
                .restartDelaySeconds(0)
                .build();

        manager.createCountdown(looping);
        assertTrue(manager.startCountdown("looping"));

        looping.setTargetInstant(Instant.now().minusSeconds(1));
        invokeManagerTick();
        server.getScheduler().performTicks(2);

        Countdown restarted = manager.getCountdown("looping").orElseThrow();
        assertTrue(restarted.isRunning(), "countdown should be running again after auto-restart");
        assertNotNull(restarted.getTargetInstant(), "auto-restarted countdown should have a target instant");
        assertTrue(restarted.getTargetInstant().isAfter(Instant.now().minusSeconds(1)));
    }

    @Test
    void endStartsConfiguredOtherCountdown() throws Exception {
        Countdown target = CountdownBuilder.builder("target")
                .type(CountdownType.MANUAL)
                .durationSeconds(15)
                .formatMessage("{formatted}")
                .startMessage("")
                .endMessage("")
                .build();
        manager.createCountdown(target);

        Countdown trigger = CountdownBuilder.builder("trigger")
                .type(CountdownType.MANUAL)
                .durationSeconds(5)
                .formatMessage("{formatted}")
                .startMessage("")
                .endMessage("")
                .startCountdown("target")
                .restartDelaySeconds(0)
                .build();

        manager.createCountdown(trigger);
        assertTrue(manager.startCountdown("trigger"));

        trigger.setTargetInstant(Instant.now().minusSeconds(1));
        invokeManagerTick();
        server.getScheduler().performTicks(2);

        Countdown afterTrigger = manager.getCountdown("trigger").orElseThrow();
        Countdown afterTarget = manager.getCountdown("target").orElseThrow();

        assertFalse(afterTrigger.isRunning(), "trigger countdown should stop after end");
        assertTrue(afterTarget.isRunning(), "target countdown should be started by start-countdown wiring");
        assertNotNull(afterTarget.getTargetInstant(), "started target countdown should receive a target instant");
    }

    private void invokeManagerTick() throws Exception {
        Method tick = CountdownManager.class.getDeclaredMethod("tick");
        tick.setAccessible(true);
        tick.invoke(manager);
    }
}
