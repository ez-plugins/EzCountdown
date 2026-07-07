package com.skyblockexp.ezcountdown.listener.actions;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import com.skyblockexp.ezcountdown.util.TimeFormat;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PreviewCountdownActionUnitTest {

    @Test
    public void emptyCountdownReturnsNone() {
        PreviewCountdownAction action = new PreviewCountdownAction(mock(MessageManager.class), mock(CountdownManager.class));
        ActionResult result = action.handle(mock(InventoryClickEvent.class), mock(Player.class), "t", Optional.empty());
        assertFalse(result.isHandled());
    }

    @Test
    public void previewUsesDurationWhenTargetMissing() {
        MessageManager messages = mock(MessageManager.class);
        CountdownManager manager = mock(CountdownManager.class);
        when(manager.getTimeFormatConfig()).thenReturn(TimeFormat.FormatConfig.DEFAULT);
        when(messages.formatWithPrefix(anyString(), anyMap())).thenReturn("rendered");

        PreviewCountdownAction action = new PreviewCountdownAction(messages, manager);
        Countdown cd = new Countdown("cd", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "{name} {formatted}", "s", "e", List.of(), ZoneId.systemDefault());
        cd.setDurationSeconds(65);
        Player player = mock(Player.class);

        ActionResult result = action.handle(mock(InventoryClickEvent.class), player, "cd", Optional.of(cd));

        assertTrue(result.isHandled());
        verify(player, org.mockito.Mockito.atLeastOnce()).sendMessage("rendered");
    }

    @Test
    public void previewUsesTargetRemainingWhenPresent() {
        MessageManager messages = mock(MessageManager.class);
        CountdownManager manager = mock(CountdownManager.class);
        when(manager.getTimeFormatConfig()).thenReturn(TimeFormat.FormatConfig.DEFAULT);
        when(messages.formatWithPrefix(org.mockito.ArgumentMatchers.contains("cd"), anyMap())).thenReturn("rendered-target");

        PreviewCountdownAction action = new PreviewCountdownAction(messages, manager);
        Countdown cd = new Countdown("cd", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "{name} {formatted}", "s", "e", List.of(), ZoneId.systemDefault());
        cd.setTargetInstant(Instant.now().plusSeconds(30));
        Player player = mock(Player.class);

        ActionResult result = action.handle(mock(InventoryClickEvent.class), player, "cd", Optional.of(cd));

        assertTrue(result.isHandled());
        verify(player).sendMessage("rendered-target");
    }
}
