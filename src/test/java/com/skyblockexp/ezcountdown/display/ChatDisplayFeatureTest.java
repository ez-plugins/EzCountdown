package com.skyblockexp.ezcountdown.display;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.display.DisplayType;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ChatDisplayFeatureTest extends MockBukkitTestBase {

    @Test
    public void chatDisplaySendsMessageToOnlinePlayers() throws Exception {
        var player = addPlayer("chatter");

        Countdown c = new Countdown("chat-test", CountdownType.MANUAL,
                EnumSet.of(DisplayType.CHAT), 1, null, "{formatted}", "start", "end",
                java.util.List.of(), ZoneId.systemDefault());
        c.setRunning(true);
        c.setDurationSeconds(5);
        c.setTargetInstant(Instant.now().plusSeconds(1));

        manager.createCountdown(c);

        // invoke a tick to cause displayManager.display(...) to be called
        java.lang.reflect.Method tick = CountdownManager.class.getDeclaredMethod("tick");
        tick.setAccessible(true);
        tick.invoke(manager);

        // PlayerMock stores component chat messages; assert one was received
        org.mockbukkit.mockbukkit.entity.PlayerMock pm = (org.mockbukkit.mockbukkit.entity.PlayerMock) player;
        assertNotNull(pm.nextComponentMessage(), "Expected a chat message to be sent to player");
    }
}
