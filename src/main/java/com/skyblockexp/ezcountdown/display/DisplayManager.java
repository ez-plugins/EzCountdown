package com.skyblockexp.ezcountdown.display;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.display.bossbar.BossBarSupport;
import com.skyblockexp.ezcountdown.display.bossbar.BossbarValidator;
import com.skyblockexp.ezcountdown.display.Validator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

@Deprecated
final class LegacyDisplayManager {

    // Deprecated: Display orchestration has moved to `com.skyblockexp.ezcountdown.manager.DisplayManager`.
    // This legacy class remains for compatibility but is not part of normal initialization.

    private final Map<String, BossBar> bossBars = new HashMap<>();
    private final Validator.ValidationResult bossbarValidation;

    public LegacyDisplayManager() {
        bossbarValidation = new BossbarValidator().validate();
        if (!bossbarValidation.isValid()) {
            Bukkit.getLogger().warning("EzCountdown: boss bar display disabled: " + bossbarValidation.getMessage());
        }
    }

    public void display(Countdown countdown, String message, long remainingSeconds) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!canSee(player, countdown)) {
                continue;
            }
            if (countdown.getDisplayTypes().contains(DisplayType.ACTION_BAR)) {
                sendActionBar(player, message);
            }
            if (countdown.getDisplayTypes().contains(DisplayType.CHAT)) {
                player.sendMessage(message);
            }
            if (countdown.getDisplayTypes().contains(DisplayType.TITLE)) {
                player.sendTitle(message, "", 10, 40, 10);
            }
            if (countdown.getDisplayTypes().contains(DisplayType.SCOREBOARD)) {
                updateScoreboard(player, countdown, message);
            }
        }
        if (countdown.getDisplayTypes().contains(DisplayType.BOSS_BAR)) {
            if (bossbarValidation.isValid()) {
                updateBossBar(countdown, message, remainingSeconds);
            }
        }
    }

    public void broadcastMessage(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    public void clearCountdown(Countdown countdown) {
        BossBar bossBar = bossBars.remove(countdown.getName());
        if (bossBar != null) {
            bossBar.removeAll();
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (countdown.getDisplayTypes().contains(DisplayType.SCOREBOARD)) {
                clearScoreboard(player, countdown);
            }
        }
    }

    public void clearAll() {
        for (BossBar bossBar : bossBars.values()) {
            bossBar.removeAll();
        }
        bossBars.clear();
    }

    private boolean canSee(Player player, Countdown countdown) {
        String permission = countdown.getVisibilityPermission();
        return permission == null || permission.isBlank() || player.hasPermission(permission);
    }

    private void updateBossBar(Countdown countdown, String message, long remainingSeconds) {
        if (!bossbarValidation.isValid()) {
            return;
        }
        BossBar bossBar = bossBars.computeIfAbsent(countdown.getName(),
                name -> Bukkit.createBossBar(message, BarColor.BLUE, BarStyle.SOLID));
        bossBar.setTitle(message);
        bossBar.setProgress(calculateProgress(countdown, remainingSeconds));
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (canSee(player, countdown)) {
                bossBar.addPlayer(player);
            } else {
                bossBar.removePlayer(player);
            }
        }
    }

    private double calculateProgress(Countdown countdown, long remainingSeconds) {
        long duration = countdown.getDurationSeconds();
        if (duration <= 0L) {
            return 1.0;
        }
        double progress = (double) remainingSeconds / (double) duration;
        if (progress < 0.0) {
            return 0.0;
        }
        return Math.min(1.0, progress);
    }

    private void updateScoreboard(Player player, Countdown countdown, String message) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) {
            return;
        }
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == manager.getMainScoreboard()) {
            scoreboard = manager.getNewScoreboard();
            player.setScoreboard(scoreboard);
        }
        String objectiveName = buildObjectiveName(countdown.getName());
        Objective objective = scoreboard.getObjective(objectiveName);
        if (objective == null) {
            objective = scoreboard.registerNewObjective(objectiveName, "dummy", ChatColor.AQUA + "Countdown");
        }
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        scoreboard.getEntries().forEach(scoreboard::resetScores);
        objective.getScore(message).setScore(1);
    }

    private void clearScoreboard(Player player, Countdown countdown) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == null) {
            return;
        }
        String objectiveName = buildObjectiveName(countdown.getName());
        Objective objective = scoreboard.getObjective(objectiveName);
        if (objective != null) {
            objective.unregister();
        }
    }

    private String buildObjectiveName(String name) {
        String normalized = name.toLowerCase();
        String base = "ezcd_" + normalized.replaceAll("[^a-z0-9]", "");
        return base.length() > 16 ? base.substring(0, 16) : base;
    }

    private void sendActionBar(Player player, String message) {
        try {
            player.sendActionBar(message);
        } catch (NoSuchMethodError err) {
            try {
                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                        net.md_5.bungee.api.chat.TextComponent.fromLegacyText(message));
            } catch (NoClassDefFoundError ignored) {
                player.sendMessage(message);
            }
        }
    }
}
