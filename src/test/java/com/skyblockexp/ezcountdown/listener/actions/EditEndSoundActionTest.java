package com.skyblockexp.ezcountdown.listener.actions;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.listener.ChatInputListener;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.junit.jupiter.api.Test;

import java.util.Locale;
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

public class EditEndSoundActionTest {

    @Test
    public void handleCoversNoneValidAndInvalidInput() {
        CountdownManager manager = mock(CountdownManager.class);
        MessageManager messages = mock(MessageManager.class);
        ChatInputListener chat = mock(ChatInputListener.class);
        com.skyblockexp.ezcountdown.bootstrap.Registry registry = mock(com.skyblockexp.ezcountdown.bootstrap.Registry.class);
        com.skyblockexp.ezcountdown.compat.scheduler.SchedulerAdapter scheduler = mock(com.skyblockexp.ezcountdown.compat.scheduler.SchedulerAdapter.class);
        com.skyblockexp.ezcountdown.gui.GuiManager gui = mock(com.skyblockexp.ezcountdown.gui.GuiManager.class);
        com.skyblockexp.ezcountdown.gui.EditorMenu editorMenu = mock(com.skyblockexp.ezcountdown.gui.EditorMenu.class);

        when(registry.scheduler()).thenReturn(scheduler);
        when(registry.gui()).thenReturn(gui);
        when(gui.editorMenu()).thenReturn(editorMenu);
        when(messages.message(eq("gui.edit.saved"), anyMap())).thenReturn("saved");

        doAnswer(call -> {
            Runnable r = call.getArgument(0);
            r.run();
            return new com.skyblockexp.ezcountdown.compat.scheduler.TaskHandle() {
                @Override public void cancel() {}
                @Override public boolean isCancelled() { return false; }
            };
        }).when(scheduler).runTask(any(Runnable.class));

        AtomicReference<Consumer<String>> captured = new AtomicReference<>();
        doAnswer(call2 -> {
            captured.set(call2.getArgument(1));
            return null;
        }).when(chat).request(any(Player.class), any());

        EditEndSoundAction action = new EditEndSoundAction(manager, messages, chat, registry);
        Player player = mock(Player.class);
        Countdown cd = new Countdown("cd", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "fmt", "start", "end", List.of(), ZoneId.systemDefault());

        ActionResult none = action.handle(mock(InventoryClickEvent.class), player, "cd", Optional.empty());
        assertFalse(none.isHandled());

        ActionResult handled = action.handle(mock(InventoryClickEvent.class), player, "cd", Optional.of(cd));
        assertTrue(handled.isHandled());

        String validSound = org.bukkit.Sound.values()[0].name().toLowerCase(Locale.ROOT);
        captured.get().accept("none");
        captured.get().accept(validSound);
        captured.get().accept("not_a_sound");

        verify(manager, org.mockito.Mockito.atLeast(2)).save();
        verify(player, org.mockito.Mockito.atLeast(2)).sendMessage("saved");
        verify(editorMenu, org.mockito.Mockito.atLeast(2)).openEditor(eq(player), any(Countdown.class));
        verify(player, org.mockito.Mockito.atLeastOnce()).sendMessage(org.mockito.ArgumentMatchers.contains("Invalid sound name"));
    }
}
