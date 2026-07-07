package com.skyblockexp.ezcountdown.integration.placeholder;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.integration.placeholder.EzCountdownPlaceholderExpansion;
import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import com.skyblockexp.ezcountdown.util.TimeFormat;
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

    @Test
    public void onPlaceholderRequest_formatted_hidesLeadingZeroDaysAndHours_whenConfigured() {
        registry.countdowns().setTimeFormatConfig(new TimeFormat.FormatConfig(
                "{days}d {hours}h {minutes}m {seconds}s", true
        ));

        Countdown cd = new Countdown("ph-hide", CountdownType.DURATION,
                java.util.EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class),
                1, null, "{formatted}", null, null, java.util.List.of(), ZoneId.systemDefault());
        cd.setDurationSeconds(65);
        cd.setRunning(true);
        cd.setTargetInstant(Instant.now().plusSeconds(65));
        registry.countdowns().createCountdown(cd);

        EzCountdownPlaceholderExpansion expansion = new EzCountdownPlaceholderExpansion(registry);
        String formatted = expansion.onPlaceholderRequest(null, "ph-hide_formatted");

        assertNotNull(formatted);
        assertFalse(formatted.isEmpty());
        assertFalse(formatted.contains("d"));
        assertFalse(formatted.contains("h"));
    }

    @Test
    public void onPlaceholderRequest_formatted_keepsLeadingZeroDaysAndHours_whenDisabled() {
        registry.countdowns().setTimeFormatConfig(new TimeFormat.FormatConfig(
                "{days}d {hours}h {minutes}m {seconds}s", false
        ));

        Countdown cd = new Countdown("ph-show", CountdownType.DURATION,
                java.util.EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class),
                1, null, "{formatted}", null, null, java.util.List.of(), ZoneId.systemDefault());
        cd.setDurationSeconds(65);
        cd.setRunning(true);
        cd.setTargetInstant(Instant.now().plusSeconds(65));
        registry.countdowns().createCountdown(cd);

        EzCountdownPlaceholderExpansion expansion = new EzCountdownPlaceholderExpansion(registry);
        String formatted = expansion.onPlaceholderRequest(null, "ph-show_formatted");

        assertNotNull(formatted);
        assertFalse(formatted.isEmpty());
        assertTrue(formatted.startsWith("0d 0h "));
    }
}
