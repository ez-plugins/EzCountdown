package com.skyblockexp.ezcountdown.listener.actions;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.display.DisplayType;
import com.skyblockexp.ezcountdown.gui.DisplayEditor;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ToggleDisplayTypeActionUnitTest {

    @Test
    public void emptyCountdownReturnsNone() {
        ToggleDisplayTypeAction action = new ToggleDisplayTypeAction(mock(CountdownManager.class), mock(MessageManager.class), mock(DisplayEditor.class));
        ActionResult result = action.handle(mock(InventoryClickEvent.class), mock(Player.class), "cd", Optional.empty());
        assertFalse(result.isHandled());
    }

    @Test
    public void outOfRangeSlotReturnsNone() {
        Countdown countdown = new Countdown("cd", CountdownType.MANUAL, EnumSet.of(DisplayType.CHAT), 1, null, "fmt", "s", "e", List.of(), ZoneId.systemDefault());
        InventoryClickEvent event = mock(InventoryClickEvent.class);
        when(event.getRawSlot()).thenReturn(100);

        ToggleDisplayTypeAction action = new ToggleDisplayTypeAction(mock(CountdownManager.class), mock(MessageManager.class), mock(DisplayEditor.class));
        ActionResult result = action.handle(event, mock(Player.class), "cd", Optional.of(countdown));

        assertFalse(result.isHandled());
    }

    @Test
    public void validSlotTogglesAndPersists() {
        Countdown countdown = new Countdown("cd", CountdownType.MANUAL, EnumSet.of(DisplayType.CHAT), 1, null, "fmt", "s", "e", List.of(), ZoneId.systemDefault());
        InventoryClickEvent event = mock(InventoryClickEvent.class);
        when(event.getRawSlot()).thenReturn(0);

        CountdownManager manager = mock(CountdownManager.class);
        MessageManager messages = mock(MessageManager.class);
        DisplayEditor editor = mock(DisplayEditor.class);
        Player player = mock(Player.class);

        when(manager.updateCountdown(eq("cd"), any(Countdown.class))).thenReturn(true);
        when(messages.message(eq("gui.display.toggled"), anyMap())).thenReturn("toggled");

        ToggleDisplayTypeAction action = new ToggleDisplayTypeAction(manager, messages, editor);
        ActionResult result = action.handle(event, player, "cd", Optional.of(countdown));

        assertTrue(result.isHandled());
        assertTrue(result.isMutated());
        verify(manager).save();
        verify(editor).openDisplayEditor(eq(player), any(Countdown.class));
    }
}
