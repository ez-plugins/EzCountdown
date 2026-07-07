package com.skyblockexp.ezcountdown.listener.actions;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.listener.ChatInputListener;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import com.skyblockexp.ezcountdown.type.CountdownTypeHandler;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EditDurationOrTargetActionUnitTest {

    @Test
    public void handleEmptyReturnsNone() {
        EditDurationOrTargetAction action = new EditDurationOrTargetAction(mock(CountdownManager.class), mock(MessageManager.class), mock(ChatInputListener.class), mock(com.skyblockexp.ezcountdown.bootstrap.Registry.class));
        ActionResult none = action.handle(mock(InventoryClickEvent.class), mock(Player.class), "cd", Optional.empty());
        assertFalse(none.isHandled());
    }

    @Test
    public void handlerPathAppliedAndNotApplied() {
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
        when(messages.message(eq("gui.edit.saved"), anyMap())).thenReturn("saved");
        when(messages.message(eq("gui.edit.invalid-duration"), anyMap())).thenReturn("invalid");
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

        EditDurationOrTargetAction action = new EditDurationOrTargetAction(manager, messages, chat, registry);
        Player player = mock(Player.class);
        Countdown cd = new Countdown("cd", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "fmt", "s", "e", List.of(), ZoneId.systemDefault());

        CountdownTypeHandler handler = mock(CountdownTypeHandler.class);
        when(manager.getHandler(cd.getType())).thenReturn(handler);
        when(handler.tryApplyEditorInput(eq("ok"), eq(cd), any())).thenReturn(true);
        when(handler.tryApplyEditorInput(eq("bad"), eq(cd), any())).thenReturn(false);

        action.handle(mock(InventoryClickEvent.class), player, "cd", Optional.of(cd));
        captured.get().accept("ok");
        captured.get().accept("bad");

        verify(manager).save();
        verify(player).sendMessage("saved");
        verify(player).sendMessage("invalid");
        verify(editor).openEditor(player, cd);
    }

    @Test
    public void noHandlerParsesDurationAndInstantAndInvalid() {
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
        when(messages.message(eq("gui.edit.saved"), anyMap())).thenReturn("saved");
        when(messages.message(eq("gui.edit.invalid-duration"), anyMap())).thenReturn("invalid");
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

        EditDurationOrTargetAction action = new EditDurationOrTargetAction(manager, messages, chat, registry);
        Player player = mock(Player.class);
        Countdown cd = new Countdown("cd", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "fmt", "s", "e", List.of(), ZoneId.systemDefault());
        cd.setRunning(true);
        when(manager.getHandler(cd.getType())).thenReturn(null);

        action.handle(mock(InventoryClickEvent.class), player, "cd", Optional.of(cd));
        captured.get().accept("60s");
        captured.get().accept("2026-01-01T00:00:00Z");
        captured.get().accept("not-a-duration-or-instant");

        assertTrue(cd.getDurationSeconds() >= 60L);
        assertNotNull(cd.getTargetInstant());
        verify(player, org.mockito.Mockito.atLeast(2)).sendMessage("saved");
        verify(player).sendMessage("invalid");
    }
}
