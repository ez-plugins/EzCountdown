package com.skyblockexp.ezcountdown.listener;

import com.skyblockexp.ezcountdown.compat.scheduler.SchedulerAdapter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class ChatInputListenerUnitTest {

    @Test
    public void onPlayerChatWithNoPendingRequestDoesNothing() {
        SchedulerAdapter scheduler = mock(SchedulerAdapter.class);
        ChatInputListener listener = new ChatInputListener(mock(org.bukkit.plugin.Plugin.class), scheduler);
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());

        AsyncPlayerChatEvent event = mock(AsyncPlayerChatEvent.class);
        when(event.getPlayer()).thenReturn(player);
        when(event.getMessage()).thenReturn("hello");
        listener.onPlayerChat(event);

        verify(event, org.mockito.Mockito.never()).setCancelled(true);
    }

    @Test
    public void onPlayerChatCancelConsumesInputWithoutCallback() {
        SchedulerAdapter scheduler = mock(SchedulerAdapter.class);
        ChatInputListener listener = new ChatInputListener(mock(org.bukkit.plugin.Plugin.class), scheduler);
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        AtomicReference<String> seen = new AtomicReference<>(null);

        listener.request(player, seen::set);

        AsyncPlayerChatEvent event = mock(AsyncPlayerChatEvent.class);
        when(event.getPlayer()).thenReturn(player);
        when(event.getMessage()).thenReturn("cancel");
        when(event.isAsynchronous()).thenReturn(false);
        listener.onPlayerChat(event);

        verify(event).setCancelled(true);
        assertEquals(null, seen.get());
    }

    @Test
    public void onPlayerChatSyncInvokesCallbackImmediately() {
        SchedulerAdapter scheduler = mock(SchedulerAdapter.class);
        ChatInputListener listener = new ChatInputListener(mock(org.bukkit.plugin.Plugin.class), scheduler);
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        AtomicReference<String> seen = new AtomicReference<>(null);

        listener.request(player, seen::set);

        AsyncPlayerChatEvent event = mock(AsyncPlayerChatEvent.class);
        when(event.getPlayer()).thenReturn(player);
        when(event.getMessage()).thenReturn("new value");
        when(event.isAsynchronous()).thenReturn(false);
        listener.onPlayerChat(event);

        verify(event).setCancelled(true);
        assertEquals("new value", seen.get());
    }

    @Test
    public void onPlayerChatAsyncUsesScheduler() {
        SchedulerAdapter scheduler = mock(SchedulerAdapter.class);
        doAnswer(invocation -> {
            Runnable r = invocation.getArgument(0);
            r.run();
            return new com.skyblockexp.ezcountdown.compat.scheduler.TaskHandle() {
                @Override public void cancel() {}
                @Override public boolean isCancelled() { return false; }
            };
        }).when(scheduler).runTask(org.mockito.ArgumentMatchers.any(Runnable.class));

        ChatInputListener listener = new ChatInputListener(mock(org.bukkit.plugin.Plugin.class), scheduler);
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        AtomicReference<String> seen = new AtomicReference<>(null);

        listener.request(player, seen::set);

        AsyncPlayerChatEvent event = mock(AsyncPlayerChatEvent.class);
        when(event.getPlayer()).thenReturn(player);
        when(event.getMessage()).thenReturn("async value");
        when(event.isAsynchronous()).thenReturn(true);
        listener.onPlayerChat(event);

        verify(event).setCancelled(true);
        assertEquals("async value", seen.get());
        verify(scheduler).runTask(org.mockito.ArgumentMatchers.any(Runnable.class));
    }
}
