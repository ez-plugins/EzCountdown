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
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PreviewCountdownActionTest {

    @Test
    public void handleCoversEmptyDurationAndTargetBranches() {
        MessageManager messages = mock(MessageManager.class);
        CountdownManager manager = mock(CountdownManager.class);
        when(manager.getTimeFormatConfig()).thenReturn(TimeFormat.FormatConfig.DEFAULT);
        PreviewCountdownAction action = new PreviewCountdownAction(messages, manager);
        Player player = mock(Player.class);

        ActionResult none = action.handle(mock(InventoryClickEvent.class), player, "cd", Optional.empty());
        assertFalse(none.isHandled());

        Countdown durationCd = new Countdown("cd", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "{name} {formatted}", "s", "e", List.of(), ZoneId.systemDefault());
        durationCd.setDurationSeconds(65);
        when(messages.formatWithPrefix(anyString(), anyMap())).thenReturn("rendered-duration");

        ActionResult durationRes = action.handle(mock(InventoryClickEvent.class), player, "cd", Optional.of(durationCd));
        assertTrue(durationRes.isHandled());
        verify(player, org.mockito.Mockito.atLeastOnce()).sendMessage("rendered-duration");

        Countdown targetCd = new Countdown("cd", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "{name} {formatted}", "s", "e", List.of(), ZoneId.systemDefault());
        targetCd.setTargetInstant(Instant.now().plusSeconds(30));
        when(messages.formatWithPrefix(contains("cd"), anyMap())).thenReturn("rendered-target");

        ActionResult targetRes = action.handle(mock(InventoryClickEvent.class), player, "cd", Optional.of(targetCd));
        assertTrue(targetRes.isHandled());
        verify(player, org.mockito.Mockito.atLeastOnce()).sendMessage("rendered-target");
    }

    @Test
    public void handleClampsPastTargetToZero() {
        MessageManager messages = mock(MessageManager.class);
        CountdownManager manager = mock(CountdownManager.class);
        when(manager.getTimeFormatConfig()).thenReturn(TimeFormat.FormatConfig.DEFAULT);
        when(messages.formatWithPrefix(contains("0s"), anyMap())).thenReturn("zero");

        PreviewCountdownAction action = new PreviewCountdownAction(messages, manager);
        Countdown cd = new Countdown("cd", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "{formatted}", "s", "e", List.of(), ZoneId.systemDefault());
        cd.setTargetInstant(Instant.now().minusSeconds(10));
        Player player = mock(Player.class);

        ActionResult result = action.handle(mock(InventoryClickEvent.class), player, "cd", Optional.of(cd));

        assertTrue(result.isHandled());
        verify(player).sendMessage("zero");
    }
}
