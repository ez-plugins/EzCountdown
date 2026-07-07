package com.skyblockexp.ezcountdown.listener.actions;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.gui.EditorMenu;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ToggleAutoRestartActionTest {

    @Test
    public void handleCoversEmptySuccessAndFailure() {
        Countdown original = new Countdown("cd", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "fmt", "s", "e", List.of(), ZoneId.systemDefault(), false, null, 0);

        CountdownManager manager = mock(CountdownManager.class);
        MessageManager messages = mock(MessageManager.class);
        EditorMenu editor = mock(EditorMenu.class);
        Player player = mock(Player.class);
        ToggleAutoRestartAction action = new ToggleAutoRestartAction(manager, messages, editor);

        ActionResult empty = action.handle(mock(InventoryClickEvent.class), player, "cd", Optional.empty());
        assertFalse(empty.isHandled());

        when(messages.message(eq("gui.edit.saved"), anyMap())).thenReturn("saved");
        when(manager.updateCountdown(eq("cd"), any(Countdown.class))).thenReturn(true);

        ActionResult success = action.handle(mock(InventoryClickEvent.class), player, "cd", Optional.of(original));
        assertTrue(success.isHandled());
        assertTrue(success.isMutated());
        verify(editor).openEditor(eq(player), any(Countdown.class));

        when(manager.updateCountdown(eq("cd"), any(Countdown.class))).thenReturn(false);
        ActionResult failed = action.handle(mock(InventoryClickEvent.class), player, "cd", Optional.of(original));
        assertTrue(failed.isHandled());
        assertFalse(failed.isMutated());
    }
}
