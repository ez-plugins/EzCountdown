package com.skyblockexp.ezcountdown.display.chat;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.display.DisplayHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChatDisplay implements DisplayHandler {

    @Override
    public void display(Countdown countdown, String message, long remainingSeconds) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String perm = countdown.getVisibilityPermission();
            if (perm == null || perm.isBlank() || player.hasPermission(perm)) {
                player.sendMessage(message);
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
