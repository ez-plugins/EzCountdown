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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ToggleRunningActionTest {

    @Test
    public void handleCoversEmptyStopAndStartBranches() {
        CountdownManager manager = mock(CountdownManager.class);
        MessageManager messages = mock(MessageManager.class);
        ToggleRunningAction action = new ToggleRunningAction(manager, messages);
        Player player = mock(Player.class);

        ActionResult none = action.handle(mock(InventoryClickEvent.class), player, "cd", Optional.empty());
        assertFalse(none.isHandled());

        when(messages.message(eq("commands.stop.success"), anyMap())).thenReturn("stopped");
        Countdown running = new Countdown("cd", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "fmt", "s", "e", List.of(), ZoneId.systemDefault());
        running.setRunning(true);

        ActionResult stopped = action.handle(mock(InventoryClickEvent.class), player, "cd", Optional.of(running));
        assertTrue(stopped.isHandled());
        assertTrue(stopped.isMutated());
        assertTrue(stopped.isCloseInventory());
        verify(manager).stopCountdown("cd");
        verify(player).sendMessage("stopped");

        when(messages.message(eq("commands.start.success"), anyMap())).thenReturn("started");
        Countdown notRunning = new Countdown("cd", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "fmt", "s", "e", List.of(), ZoneId.systemDefault());
        notRunning.setRunning(false);

        ActionResult started = action.handle(mock(InventoryClickEvent.class), player, "cd", Optional.of(notRunning));
        assertTrue(started.isHandled());
        assertTrue(started.isMutated());
        assertTrue(started.isCloseInventory());
        verify(manager).startCountdown("cd");
        verify(player).sendMessage("started");
    }

    @Test
    public void previewActionCoversEmptyDurationAndTargetPaths() {
        MessageManager messages = mock(MessageManager.class);
        CountdownManager manager = mock(CountdownManager.class);
        when(manager.getTimeFormatConfig()).thenReturn(TimeFormat.FormatConfig.DEFAULT);
        PreviewCountdownAction preview = new PreviewCountdownAction(messages, manager);
        Player player = mock(Player.class);

        ActionResult none = preview.handle(mock(InventoryClickEvent.class), player, "cd", Optional.empty());
        assertFalse(none.isHandled());

        Countdown durationCd = new Countdown("cd", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "{name} {formatted}", "s", "e", List.of(), ZoneId.systemDefault());
        durationCd.setDurationSeconds(65);
        when(messages.formatWithPrefix(contains("cd"), anyMap())).thenReturn("rendered-duration");

        ActionResult durationRes = preview.handle(mock(InventoryClickEvent.class), player, "cd", Optional.of(durationCd));
        assertTrue(durationRes.isHandled());
        verify(player, org.mockito.Mockito.atLeastOnce()).sendMessage("rendered-duration");

        Countdown targetCd = new Countdown("cd", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "{name} {formatted}", "s", "e", List.of(), ZoneId.systemDefault());
        targetCd.setTargetInstant(Instant.now().minusSeconds(10));
        when(messages.formatWithPrefix(contains("0"), anyMap())).thenReturn("rendered-target");

        ActionResult targetRes = preview.handle(mock(InventoryClickEvent.class), player, "cd", Optional.of(targetCd));
        assertTrue(targetRes.isHandled());
        verify(player, org.mockito.Mockito.atLeastOnce()).sendMessage("rendered-target");
    }
}
