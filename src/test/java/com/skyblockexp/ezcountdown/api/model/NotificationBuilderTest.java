package com.skyblockexp.ezcountdown.api.model;

import com.skyblockexp.ezcountdown.display.DisplayType;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationBuilderTest {

    // ------------------------------------------------------------------
    // Defaults
    // ------------------------------------------------------------------

    @Test
    public void ofSeconds_usesDefaultDisplayAndFormat() {
        Notification n = Notification.ofSeconds(30);
        assertEquals(30L, n.getDurationSeconds());
        assertEquals(EnumSet.of(DisplayType.ACTION_BAR), n.getDisplayTypes());
        assertEquals("{formatted}", n.getFormatMessage());
        assertNull(n.getStartMessage());
        assertNull(n.getEndMessage());
    }

    @Test
    public void of_duration_works() {
        Notification n = Notification.of(Duration.ofMinutes(2));
        assertEquals(120L, n.getDurationSeconds());
    }

    @Test
    public void builder_defaultsMatchOfSeconds() {
        Notification fromHelper = Notification.ofSeconds(10);
        Notification fromBuilder = Notification.builder().duration(10).build();

        assertEquals(fromHelper.getDurationSeconds(), fromBuilder.getDurationSeconds());
        assertEquals(fromHelper.getDisplayTypes(), fromBuilder.getDisplayTypes());
        assertEquals(fromHelper.getFormatMessage(), fromBuilder.getFormatMessage());
    }

    // ------------------------------------------------------------------
    // Duration
    // ------------------------------------------------------------------

    @Test
    public void builder_durationLong() {
        Notification n = Notification.builder().duration(45).build();
        assertEquals(45L, n.getDurationSeconds());
    }

    @Test
    public void builder_durationDuration() {
        Notification n = Notification.builder().duration(Duration.ofSeconds(90)).build();
        assertEquals(90L, n.getDurationSeconds());
    }

    @Test
    public void builder_throwsWithoutDuration() {
        assertThrows(IllegalStateException.class, () -> Notification.builder().build());
    }

    @Test
    public void notification_throwsForNonPositiveDuration() {
        assertThrows(IllegalArgumentException.class, () -> Notification.ofSeconds(0));
        assertThrows(IllegalArgumentException.class, () -> Notification.ofSeconds(-5));
    }

    // ------------------------------------------------------------------
    // Display types
    // ------------------------------------------------------------------

    @Test
    public void builder_display_varargs() {
        Notification n = Notification.builder()
                .duration(10)
                .display(DisplayType.BOSS_BAR, DisplayType.TITLE)
                .build();
        assertTrue(n.getDisplayTypes().contains(DisplayType.BOSS_BAR));
        assertTrue(n.getDisplayTypes().contains(DisplayType.TITLE));
        assertFalse(n.getDisplayTypes().contains(DisplayType.ACTION_BAR));
    }

    @Test
    public void builder_displays_enumSet() {
        EnumSet<DisplayType> types = EnumSet.of(DisplayType.CHAT, DisplayType.SCOREBOARD);
        Notification n = Notification.builder().duration(10).displays(types).build();
        assertEquals(types, n.getDisplayTypes());
    }

    @Test
    public void builder_addDisplay_appendsToDefault() {
        Notification n = Notification.builder()
                .duration(10)
                .addDisplay(DisplayType.BOSS_BAR)
                .build();
        // ACTION_BAR (default) + BOSS_BAR
        assertTrue(n.getDisplayTypes().contains(DisplayType.ACTION_BAR));
        assertTrue(n.getDisplayTypes().contains(DisplayType.BOSS_BAR));
    }

    @Test
    public void builder_displays_null_resetsToDefault() {
        Notification n = Notification.builder().duration(10).displays(null).build();
        assertEquals(Notification.DEFAULT_DISPLAY_TYPES, n.getDisplayTypes());
    }

    // ------------------------------------------------------------------
    // Messages
    // ------------------------------------------------------------------

    @Test
    public void builder_message() {
        Notification n = Notification.builder().duration(10).message("&eTime: {formatted}").build();
        assertEquals("&eTime: {formatted}", n.getFormatMessage());
    }

    @Test
    public void builder_message_null_resetsToDefault() {
        Notification n = Notification.builder().duration(10).message(null).build();
        assertEquals(Notification.DEFAULT_FORMAT_MESSAGE, n.getFormatMessage());
    }

    @Test
    public void builder_startAndEndMessage() {
        Notification n = Notification.builder()
                .duration(10)
                .startMessage("&aStart!")
                .endMessage("&cEnd!")
                .build();
        assertEquals("&aStart!", n.getStartMessage());
        assertEquals("&cEnd!", n.getEndMessage());
    }

    @Test
    public void builder_blankStartMessage_treatedAsNull() {
        Notification n = Notification.builder().duration(10).startMessage("   ").build();
        assertNull(n.getStartMessage());
    }

    @Test
    public void getDisplayTypes_returnsDefensiveCopy() {
        Notification n = Notification.ofSeconds(10);
        EnumSet<DisplayType> copy = n.getDisplayTypes();
        copy.add(DisplayType.CHAT); // mutate the returned copy
        // original should be unaffected
        assertFalse(n.getDisplayTypes().contains(DisplayType.CHAT));
    }

    // ------------------------------------------------------------------
    // Per-player targeting
    // ------------------------------------------------------------------

    @Test
    public void builder_players_storesUUIDs() {
        org.bukkit.entity.Player p = org.mockito.Mockito.mock(org.bukkit.entity.Player.class);
        java.util.UUID uid = java.util.UUID.randomUUID();
        org.mockito.Mockito.when(p.getUniqueId()).thenReturn(uid);

        Notification n = Notification.builder().duration(10).players(java.util.List.of(p)).build();

        assertNotNull(n.getTargetPlayers());
        assertTrue(n.getTargetPlayers().contains(uid));
    }

    @Test
    public void builder_nullPlayers_noTargetPlayers() {
        Notification n = Notification.builder().duration(10).players(null).build();
        assertNull(n.getTargetPlayers());
    }

    @Test
    public void ofSeconds_noTargetPlayers() {
        Notification n = Notification.ofSeconds(10);
        assertNull(n.getTargetPlayers());
    }
}
