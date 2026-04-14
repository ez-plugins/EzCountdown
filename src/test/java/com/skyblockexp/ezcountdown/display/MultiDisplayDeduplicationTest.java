package com.skyblockexp.ezcountdown.display;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Regression test: a countdown with multiple display types that include CHAT should
 * deliver exactly ONE chat message per player per tick — not one message per active
 * display type.
 */
public class MultiDisplayDeduplicationTest extends MockBukkitTestBase {

    @Test
    public void chatAndActionBar_playerReceivesOnlyOneMessagePerTick() throws Exception {
        var player = addPlayer("p1");

        // Countdown with both CHAT and ACTION_BAR enabled; blank start/end messages
        // to avoid fireStart() broadcasting a chat message that would skew the count.
        Countdown c = new Countdown("multi", CountdownType.MANUAL,
                EnumSet.of(DisplayType.CHAT, DisplayType.ACTION_BAR),
                1, null, "{formatted}", "", "",
                List.of(), ZoneId.systemDefault());
        c.setRunning(true);
        c.setDurationSeconds(10);
        c.setTargetInstant(Instant.now().plusSeconds(5));

        manager.createCountdown(c);

        java.lang.reflect.Method tick = CountdownManager.class.getDeclaredMethod("tick");
        tick.setAccessible(true);
        tick.invoke(manager);

        org.mockbukkit.mockbukkit.entity.PlayerMock pm = (org.mockbukkit.mockbukkit.entity.PlayerMock) player;

        // Exactly one chat message expected (from CHAT display type via the batch)
        assertNotNull(pm.nextComponentMessage(), "Expected exactly one chat message");
        // No second message — deduplication is working
        assertNull(pm.nextComponentMessage(), "Expected no second chat message (deduplication failure)");
    }

    @Test
    public void chatTitleAndActionBar_playerReceivesOnlyOneMessagePerTick() throws Exception {
        var player = addPlayer("p2");

        Countdown c = new Countdown("multi2", CountdownType.MANUAL,
                EnumSet.of(DisplayType.CHAT, DisplayType.ACTION_BAR, DisplayType.TITLE),
                1, null, "{formatted}", "", "",
                List.of(), ZoneId.systemDefault());
        c.setRunning(true);
        c.setDurationSeconds(10);
        c.setTargetInstant(Instant.now().plusSeconds(5));

        manager.createCountdown(c);

        java.lang.reflect.Method tick = CountdownManager.class.getDeclaredMethod("tick");
        tick.setAccessible(true);
        tick.invoke(manager);

        org.mockbukkit.mockbukkit.entity.PlayerMock pm = (org.mockbukkit.mockbukkit.entity.PlayerMock) player;

        assertNotNull(pm.nextComponentMessage(), "Expected exactly one chat message");
        assertNull(pm.nextComponentMessage(), "Expected no second chat message (deduplication failure)");
    }
}
