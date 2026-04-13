package com.skyblockexp.ezcountdown.display.title;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.display.DisplayHandler;
import com.skyblockexp.ezcountdown.display.MessageBatch;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TitleDisplay implements DisplayHandler {

    @Override
    public void display(Countdown countdown, String message, long remainingSeconds) {
        if (remainingSeconds <= 0L) return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            String perm = countdown.getVisibilityPermission();
            if (perm == null || perm.isBlank() || player.hasPermission(perm)) {
                try {
                    player.sendTitle(message, "", 10, 40, 10);
                } catch (NoSuchMethodError | NoClassDefFoundError err) {
                    // Fallback to action bar if available, otherwise send chat
                    try {
                        player.sendActionBar(message);
                    } catch (NoSuchMethodError | NoClassDefFoundError ex) {
                        player.sendMessage(message);
                    }
                }
            }
        }
    }

    @Override
    public void displayBatched(Countdown countdown, String message, long remainingSeconds, MessageBatch batch) {
        if (remainingSeconds <= 0L) return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            String perm = countdown.getVisibilityPermission();
            if (perm == null || perm.isBlank() || player.hasPermission(perm)) {
                try {
                    player.sendTitle(message, "", 10, 40, 10);
                } catch (NoSuchMethodError | NoClassDefFoundError err) {
                    // Fallback to action bar if available, otherwise route through batch
                    try {
                        player.sendActionBar(message);
                    } catch (NoSuchMethodError | NoClassDefFoundError ex) {
                        batch.add(player, countdown, message);
                    }
                }
            }
        }
    }

    @Override
    public void clear(Countdown countdown) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String perm = countdown.getVisibilityPermission();
            if (perm == null || perm.isBlank() || player.hasPermission(perm)) {
                try {
                    player.resetTitle();
                } catch (NoSuchMethodError | NoClassDefFoundError ignored) {
                    // nothing to clear when titles aren't supported
                }
            }
        }
    }

    @Override
    public void clearAll() {}
}
