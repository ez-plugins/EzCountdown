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

public class TitleDisplayFeatureTest extends MockBukkitTestBase {

    @Test
    public void titleDisplaySendsTitleToOnlinePlayers() throws Exception {
        var player = addPlayer("titler");

        Countdown c = new Countdown("title-test", CountdownType.MANUAL,
                EnumSet.of(DisplayType.TITLE), 1, null, "{formatted}", "start", "end",
                java.util.List.of(), ZoneId.systemDefault());
        c.setRunning(true);
        c.setDurationSeconds(5);
        c.setTargetInstant(Instant.now().plusSeconds(1));

        manager.createCountdown(c);

        // invoke a tick to cause displayManager.display(...) to be called
        java.lang.reflect.Method tick = CountdownManager.class.getDeclaredMethod("tick");
        tick.setAccessible(true);
        tick.invoke(manager);

        org.mockbukkit.mockbukkit.entity.PlayerMock pm = (org.mockbukkit.mockbukkit.entity.PlayerMock) player;
        // MockBukkit stores title messages; assert one was received
        assertNotNull(pm.nextTitle(), "Expected a title message to be sent to player");
    }
}
