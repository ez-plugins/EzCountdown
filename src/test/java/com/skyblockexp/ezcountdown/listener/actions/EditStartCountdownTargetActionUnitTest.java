package com.skyblockexp.ezcountdown.listener.actions;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.listener.ChatInputListener;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EditStartCountdownTargetActionUnitTest {

    @Test
    public void handlesEmptyAndMissingTargetAndExistingTarget() {
        CountdownManager manager = mock(CountdownManager.class);
        MessageManager messages = mock(MessageManager.class);
        ChatInputListener chat = mock(ChatInputListener.class);
        com.skyblockexp.ezcountdown.bootstrap.Registry registry = mock(com.skyblockexp.ezcountdown.bootstrap.Registry.class);
        com.skyblockexp.ezcountdown.compat.scheduler.SchedulerAdapter scheduler = mock(com.skyblockexp.ezcountdown.compat.scheduler.SchedulerAdapter.class);
        com.skyblockexp.ezcountdown.gui.GuiManager gui = mock(com.skyblockexp.ezcountdown.gui.GuiManager.class);
        com.skyblockexp.ezcountdown.gui.EditorMenu editor = mock(com.skyblockexp.ezcountdown.gui.EditorMenu.class);
        when(registry.scheduler()).thenReturn(scheduler);
        when(registry.gui()).thenReturn(gui);
        when(gui.editorMenu()).thenReturn(editor);
        when(manager.updateCountdown(eq("cd"), any(Countdown.class))).thenReturn(true);
        when(messages.message(eq("gui.edit.saved"), anyMap())).thenReturn("saved");
        when(messages.message(eq("gui.edit.start-countdown.missing"), anyMap())).thenReturn("missing");
        doAnswer(inv -> {
            Runnable r = inv.getArgument(0);
            r.run();
            return new com.skyblockexp.ezcountdown.compat.scheduler.TaskHandle() {
                @Override public void cancel() {}
                @Override public boolean isCancelled() { return false; }
            };
        }).when(scheduler).runTask(any(Runnable.class));

        AtomicReference<Consumer<String>> captured = new AtomicReference<>();
        doAnswer(call2 -> { captured.set(call2.getArgument(1)); return null; }).when(chat).request(any(Player.class), any());

        EditStartCountdownTargetAction action = new EditStartCountdownTargetAction(manager, messages, chat, registry);
        Player player = mock(Player.class);
        Countdown cd = new Countdown("cd", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "fmt", "s", "e", List.of(), ZoneId.systemDefault(), false, null, 0);

        ActionResult none = action.handle(mock(InventoryClickEvent.class), player, "cd", Optional.empty());
        assertFalse(none.isHandled());

        ActionResult handled = action.handle(mock(InventoryClickEvent.class), player, "cd", Optional.of(cd));
        assertTrue(handled.isHandled());

        when(manager.getCountdown("other")).thenReturn(Optional.of(new Countdown("other", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "fmt", "s", "e", List.of(), ZoneId.systemDefault())));
        captured.get().accept("other");
        captured.get().accept("missingOne");

        verify(manager, org.mockito.Mockito.atLeast(2)).save();
        verify(player, org.mockito.Mockito.atLeast(2)).sendMessage("saved");
        verify(player).sendMessage("missing");
        verify(editor, org.mockito.Mockito.atLeastOnce()).openEditor(eq(player), any(Countdown.class));
    }
}
