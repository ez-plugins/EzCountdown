package com.skyblockexp.ezcountdown.display.bossbar;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.display.DisplayHandler;
import com.skyblockexp.ezcountdown.display.StackableDisplay;
import com.skyblockexp.ezcountdown.display.bossbar.BossBarSupport;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarDisplay implements StackableDisplay {
    private final Map<String, BossBar> bossBars = new HashMap<>();

    private void updateSingle(Countdown countdown, String message, long remainingSeconds) {
        if (!BossBarSupport.isSupported()) return;
        // remove bossbar when timer is zero
        if (remainingSeconds <= 0L) {
            BossBar removed = bossBars.remove(countdown.getName());
            if (removed != null) removed.removeAll();
            return;
        }
        try {
            BossBar bossBar = bossBars.get(countdown.getName());
            if (bossBar == null) {
                bossBar = Bukkit.createBossBar(message, countdown.getBossBarColor(), countdown.getBossBarStyle());
                bossBars.put(countdown.getName(), bossBar);
            }
            bossBar.setTitle(message);
            bossBar.setProgress(calculateProgress(countdown, remainingSeconds));
            for (Player player : Bukkit.getOnlinePlayers()) {
                String perm = countdown.getVisibilityPermission();
                try {
                    if (perm == null || perm.isBlank() || player.hasPermission(perm)) {
                        bossBar.addPlayer(player);
                    } else {
                        bossBar.removePlayer(player);
                    }
                } catch (NoClassDefFoundError | IllegalStateException ignored) {
                    // ignore per-player errors
                }
            }
        } catch (NoClassDefFoundError | IllegalStateException e) {
            // BossBar API unexpectedly unavailable at runtime; silently ignore to avoid server errors
        }
    }

    @Override
    public void display(Countdown countdown, String message, long remainingSeconds) {
        updateSingle(countdown, message, remainingSeconds);
    }

    @Override
    public void clear(Countdown countdown) {
        BossBar bossBar = bossBars.remove(countdown.getName());
        if (bossBar != null) bossBar.removeAll();
    }

    @Override
    public void clearAll() {
        for (BossBar bossBar : bossBars.values()) {
            bossBar.removeAll();
        }
        bossBars.clear();
    }

    @Override
    public void displayMultiple(java.util.Collection<Countdown> countdowns, java.util.Map<Countdown, String> messages, java.util.Map<Countdown, Long> remaining) {
        if (!BossBarSupport.isSupported()) return;
        for (Countdown c : countdowns) {
            if (!c.getDisplayTypes().contains(com.skyblockexp.ezcountdown.display.DisplayType.BOSS_BAR)) continue;
            String msg = messages.get(c);
            long rem = remaining.getOrDefault(c, 0L);
            updateSingle(c, msg, rem);
        }
    }

    private double calculateProgress(Countdown countdown, long remainingSeconds) {
        long durationSeconds = countdown.getDurationSeconds();
        if (durationSeconds <= 0L) return 1.0;
        java.time.Instant target = countdown.getTargetInstant();
        if (target == null) {
            double progress = (double) remainingSeconds / (double) durationSeconds;
            return Math.min(1.0, Math.max(0.0, progress));
        }
        // Use millisecond precision for smooth animation when called at sub-second frequency
        long nowMillis = java.time.Instant.now().toEpochMilli();
        long remainingMillis = Math.max(0L, target.toEpochMilli() - nowMillis);
        double progress = (double) remainingMillis / ((double) durationSeconds * 1000.0);
        return Math.min(1.0, Math.max(0.0, progress));
    }
}
