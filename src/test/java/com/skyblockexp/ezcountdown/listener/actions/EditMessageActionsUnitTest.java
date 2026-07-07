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

public class EditMessageActionsUnitTest {

    @Test
    public void startMessageActionRequestsInputAndSaves() {
        runTextAction(new EditStartMessageActionFixture());
    }

    @Test
    public void endMessageActionRequestsInputAndSaves() {
        runTextAction(new EditEndMessageActionFixture());
    }

    @Test
    public void formatMessageActionRequestsInputAndSaves() {
        runTextAction(new EditFormatMessageActionFixture());
    }

    private void runTextAction(Fixture fixture) {
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
        when(manager.updateCountdown(eq("cd"), any(Countdown.class))).thenReturn(true);
        when(messages.message(eq("gui.edit.saved"), anyMap())).thenReturn("saved");
        doAnswer(inv -> {
            Runnable r = inv.getArgument(0);
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

        Player player = mock(Player.class);
        Countdown cd = new Countdown("cd", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "fmt", "start", "end", List.of(), ZoneId.systemDefault());

        ActionResult none = fixture.create(manager, messages, chat, registry).handle(mock(InventoryClickEvent.class), player, "cd", Optional.empty());
        assertFalse(none.isHandled());

        ActionResult handled = fixture.create(manager, messages, chat, registry).handle(mock(InventoryClickEvent.class), player, "cd", Optional.of(cd));
        assertTrue(handled.isHandled());
        captured.get().accept("&aupdated");

        verify(manager).save();
        verify(player).sendMessage("saved");
        verify(editorMenu).openEditor(eq(player), any(Countdown.class));
    }

    private interface Fixture {
        GuiAction create(CountdownManager manager, MessageManager messages, ChatInputListener chat, com.skyblockexp.ezcountdown.bootstrap.Registry registry);
    }

    private static class EditStartMessageActionFixture implements Fixture {
        @Override
        public GuiAction create(CountdownManager manager, MessageManager messages, ChatInputListener chat, com.skyblockexp.ezcountdown.bootstrap.Registry registry) {
            return new EditStartMessageAction(manager, messages, chat, registry);
        }
    }

    private static class EditEndMessageActionFixture implements Fixture {
        @Override
        public GuiAction create(CountdownManager manager, MessageManager messages, ChatInputListener chat, com.skyblockexp.ezcountdown.bootstrap.Registry registry) {
            return new EditEndMessageAction(manager, messages, chat, registry);
        }
    }

    private static class EditFormatMessageActionFixture implements Fixture {
        @Override
        public GuiAction create(CountdownManager manager, MessageManager messages, ChatInputListener chat, com.skyblockexp.ezcountdown.bootstrap.Registry registry) {
            return new EditFormatMessageAction(manager, messages, chat, registry);
        }
    }
}
