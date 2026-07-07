package com.skyblockexp.ezcountdown.listener.actions;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ToggleRunningActionUnitTest {

    @Test
    public void emptyCountdownReturnsNone() {
        ToggleRunningAction action = new ToggleRunningAction(mock(CountdownManager.class), mock(MessageManager.class));
        ActionResult result = action.handle(mock(InventoryClickEvent.class), mock(Player.class), "cd", Optional.empty());
        assertFalse(result.isHandled());
    }

    @Test
    public void runningCountdownStops() {
        CountdownManager manager = mock(CountdownManager.class);
        MessageManager messages = mock(MessageManager.class);
        ToggleRunningAction action = new ToggleRunningAction(manager, messages);
        Player player = mock(Player.class);
        when(messages.message(eq("commands.stop.success"), anyMap())).thenReturn("stopped");

        Countdown cd = new Countdown("cd", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "fmt", "s", "e", List.of(), ZoneId.systemDefault());
        cd.setRunning(true);

        ActionResult result = action.handle(mock(InventoryClickEvent.class), player, "cd", Optional.of(cd));

        assertTrue(result.isHandled());
        assertTrue(result.isMutated());
        assertTrue(result.isCloseInventory());
        verify(manager).stopCountdown("cd");
        verify(player).sendMessage("stopped");
    }

    @Test
    public void stoppedCountdownStarts() {
        CountdownManager manager = mock(CountdownManager.class);
        MessageManager messages = mock(MessageManager.class);
        ToggleRunningAction action = new ToggleRunningAction(manager, messages);
        Player player = mock(Player.class);
        when(messages.message(eq("commands.start.success"), anyMap())).thenReturn("started");

        Countdown cd = new Countdown("cd", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "fmt", "s", "e", List.of(), ZoneId.systemDefault());
        cd.setRunning(false);

        ActionResult result = action.handle(mock(InventoryClickEvent.class), player, "cd", Optional.of(cd));

        assertTrue(result.isHandled());
        assertTrue(result.isMutated());
        verify(manager).startCountdown("cd");
        verify(player).sendMessage("started");
    }
}
