package com.skyblockexp.ezcountdown.display.bossbar;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.display.DisplayHandler;
import com.skyblockexp.ezcountdown.display.bossbar.BossBarSupport;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarDisplay implements DisplayHandler {
    private final Map<String, BossBar> bossBars = new HashMap<>();

    @Override
    public void display(Countdown countdown, String message, long remainingSeconds) {
        if (!BossBarSupport.isSupported()) return;
        try {
            BossBar bossBar = bossBars.computeIfAbsent(countdown.getName(),
                    name -> Bukkit.createBossBar(message, BarColor.BLUE, BarStyle.SOLID));
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

    private double calculateProgress(Countdown countdown, long remainingSeconds) {
        long duration = countdown.getDurationSeconds();
        if (duration <= 0L) return 1.0;
        double progress = (double) remainingSeconds / (double) duration;
        if (progress < 0.0) return 0.0;
        return Math.min(1.0, progress);
    }
}
