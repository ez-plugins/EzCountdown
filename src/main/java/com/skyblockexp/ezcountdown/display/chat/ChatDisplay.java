package com.skyblockexp.ezcountdown.display.chat;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.display.DisplayHandler;
import com.skyblockexp.ezcountdown.display.MessageBatch;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChatDisplay implements DisplayHandler {

    @Override
    public void display(Countdown countdown, String message, long remainingSeconds) {
        if (remainingSeconds <= 0L) return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            String perm = countdown.getVisibilityPermission();
            if (perm == null || perm.isBlank() || player.hasPermission(perm)) {
                player.sendMessage(message);
            }
        }
    }

    @Override
    public void displayBatched(Countdown countdown, String message, long remainingSeconds, MessageBatch batch) {
        if (remainingSeconds <= 0L) return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            String perm = countdown.getVisibilityPermission();
            if (perm == null || perm.isBlank() || player.hasPermission(perm)) {
                batch.add(player, countdown, message);
            }
        }
    }

    @Override
    public void clear(Countdown countdown) {
        // chat messages cannot be cleared
    }

    @Override
    public void clearAll() {}
}
