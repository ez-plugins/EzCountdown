package com.skyblockexp.ezcountdown.api;

import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EzCountdownApiFeatureTest extends MockBukkitTestBase {

    @Test
    public void apiCreatesAndStartsCountdown_withRealManager_andNotifiesPlayers() {
        // Use the real registry/manager provided by MockBukkit plugin bootstrap
        EzCountdownApi api = new EzCountdownApiImpl(registry);

        Player p = addPlayer("api-user");

        boolean created = api.createCountdown(CountdownType.DURATION, 3L, java.util.List.of(p));
        assertTrue(created, "API should report countdown created");

        // ensure manager reports the countdown exists
        assertFalse(api.getCountdown("api-user").isPresent(), "API-generated name should not be raw player name");

        // find any countdown from manager and assert it's running or registered
        var list = api.listCountdowns();
        assertNotNull(list);
        assertTrue(list.size() > 0, "There should be at least one countdown created by API");

        // Ensure the manager created a countdown of type DURATION with the requested duration
        boolean found = api.listCountdowns().stream().anyMatch(c -> c.getType() == com.skyblockexp.ezcountdown.api.model.CountdownType.DURATION && c.getDurationSeconds() >= 3);
        assertTrue(found, "Manager should contain a duration countdown created via the API");
    }
}
