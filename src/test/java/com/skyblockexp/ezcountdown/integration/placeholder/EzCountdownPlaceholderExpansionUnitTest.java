package com.skyblockexp.ezcountdown.integration.placeholder;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.integration.placeholder.EzCountdownPlaceholderExpansion;
import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

public class EzCountdownPlaceholderExpansionUnitTest extends MockBukkitTestBase {

    @Test
    public void onPlaceholderRequest_returnsFormattedAndParts() {
        // create countdown and register it in manager
        Countdown cd = new Countdown("ph-test", CountdownType.DURATION, java.util.EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "{formatted}", null, null, java.util.List.of(), ZoneId.systemDefault());
        cd.setDurationSeconds(65);
        cd.setRunning(true);
        cd.setTargetInstant(Instant.now().plusSeconds(65));
        registry.countdowns().createCountdown(cd);

        EzCountdownPlaceholderExpansion expansion = new EzCountdownPlaceholderExpansion(registry);

        String formatted = expansion.onPlaceholderRequest(null, "ph-test_formatted");
        assertNotNull(formatted);
        assertFalse(formatted.isEmpty());

        String seconds = expansion.onPlaceholderRequest(null, "ph-test_seconds");
        assertNotNull(seconds);
        assertTrue(Integer.parseInt(seconds) >= 0);
    }
}
