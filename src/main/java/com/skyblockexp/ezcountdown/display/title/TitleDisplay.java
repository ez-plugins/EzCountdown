package com.skyblockexp.ezcountdown.display.title;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.compat.title.TitleCompat;
import com.skyblockexp.ezcountdown.display.DisplayHandler;
import com.skyblockexp.ezcountdown.display.MessageBatch;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TitleDisplay implements DisplayHandler {

    @Override
    public void display(Countdown countdown, String message, long remainingSeconds) {
        if (remainingSeconds <= 0L) return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (countdown.isVisibleTo(player)) {
                TitleCompat.sendTitle(player, message, 10, 40, 10);
            }
        }
    }

    @Override
    public void displayBatched(Countdown countdown, String message, long remainingSeconds, MessageBatch batch) {
        if (remainingSeconds <= 0L) return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (countdown.isVisibleTo(player)) {
                TitleCompat.sendTitle(player, message, 10, 40, 10);
            }
        }
    }

    @Override
    public void clear(Countdown countdown) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (countdown.isVisibleTo(player)) {
                TitleCompat.clearTitle(player);
            }
        }
    }

    @Override
    public void clearAll() {}
}
