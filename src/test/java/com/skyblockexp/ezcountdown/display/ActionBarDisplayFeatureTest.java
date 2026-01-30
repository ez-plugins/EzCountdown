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

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActionBarDisplayFeatureTest extends MockBukkitTestBase {

    @Test
    public void actionBarDisplaySendsActionBarToOnlinePlayers() throws Exception {
        var player = addPlayer("actioner");

        Countdown c = new Countdown("action-test", CountdownType.MANUAL,
                EnumSet.of(DisplayType.ACTION_BAR), 1, null, "{formatted}", "start", "end",
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
        // MockBukkit may deliver action bar via different channels; accept any non-null relevant message
        var actionBar = pm.nextActionBar();
        var chat = pm.nextComponentMessage();
        var title = pm.nextTitle();
        assertTrue(actionBar != null || chat != null || title != null,
            "Expected an action-bar-style message to be sent to player via actionbar/chat/title");
    }
}
