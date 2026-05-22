package com.skyblockexp.ezcountdown.listener;

import com.skyblockexp.ezcountdown.compat.scheduler.SchedulerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.bukkit.plugin.Plugin;

public final class ChatInputListener implements Listener {
    private final Map<UUID, Consumer<String>> pending = new ConcurrentHashMap<>();

    private final Plugin plugin;
    private final SchedulerAdapter scheduler;

    public ChatInputListener(Plugin plugin, SchedulerAdapter scheduler) {
        this.plugin = plugin;
        this.scheduler = scheduler;
    }

    public void request(Player player, Consumer<String> callback) {
        // Register a one-time chat consumer for this player and prompt them to type.
        pending.put(player.getUniqueId(), callback);
        try {
            player.sendMessage("Please type your input in chat. Type 'cancel' to cancel.");
        } catch (Throwable ignored) {
            // best-effort: if sending chat fails, do nothing
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        if (!pending.containsKey(id)) return;
        String msg = event.getMessage();
        // consume the chat message so it doesn't broadcast
        event.setCancelled(true);
        Consumer<String> cb = pending.remove(id);
        if (cb == null) return;
        if (msg.equalsIgnoreCase("cancel")) {
            try { player.sendMessage("Input cancelled."); } catch (Throwable ignored) {}
            return;
        }
        // If this event was delivered on the main thread, invoke synchronously, otherwise schedule on main thread
        if (!event.isAsynchronous()) {
            try { cb.accept(msg); } catch (Throwable t) { t.printStackTrace(); }
        } else {
            scheduler.runTask(() -> {
                try { cb.accept(msg); } catch (Throwable t) { t.printStackTrace(); }
            });
        }
    }
}
