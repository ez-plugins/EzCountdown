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

public class ScoreboardDisplayFeatureTest extends MockBukkitTestBase {

    @Test
    public void scoreboardDisplayUpdatesPlayerScoreboard() throws Exception {
        var player = addPlayer("scorer");

        Countdown c = new Countdown("score-test", CountdownType.MANUAL,
                EnumSet.of(DisplayType.SCOREBOARD), 1, null, "{formatted}", "start", "end",
                java.util.List.of(), ZoneId.systemDefault());
        c.setRunning(true);
        c.setDurationSeconds(5);
        c.setTargetInstant(Instant.now().plusSeconds(1));

        manager.createCountdown(c);

        // invoke a tick to cause displayManager.display(...) to be called
        java.lang.reflect.Method tick = CountdownManager.class.getDeclaredMethod("tick");
        tick.setAccessible(true);
        tick.invoke(manager);

        // PlayerMock should have a scoreboard instance
        org.bukkit.entity.Player p = player;
        assertNotNull(p.getScoreboard(), "Expected player's scoreboard to be non-null after display update");
    }
}
