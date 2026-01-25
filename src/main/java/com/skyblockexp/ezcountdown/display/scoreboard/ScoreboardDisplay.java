package com.skyblockexp.ezcountdown.display.scoreboard;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.display.DisplayHandler;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScoreboardDisplay implements DisplayHandler {

    @Override
    public void display(Countdown countdown, String message, long remainingSeconds) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            String perm = countdown.getVisibilityPermission();
            if (perm == null || perm.isBlank() || player.hasPermission(perm)) {
                try {
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
                } catch (NoClassDefFoundError | UnsupportedOperationException | IllegalArgumentException e) {
                    // Scoreboard operations failed; fall back to chat for compatibility
                    player.sendMessage(message);
                }
            }
        }
    }

    @Override
    public void clear(Countdown countdown) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Scoreboard scoreboard = player.getScoreboard();
            if (scoreboard == null) continue;
            String objectiveName = buildObjectiveName(countdown.getName());
            Objective objective = scoreboard.getObjective(objectiveName);
            if (objective != null) {
                objective.unregister();
            }
        }
    }

    @Override
    public void clearAll() {
        // per-player scoreboards will be reset by clear routines
    }

    private String buildObjectiveName(String name) {
        String normalized = name.toLowerCase(Locale.ROOT);
        String base = "ezcd_" + normalized.replaceAll("[^a-z0-9]", "");
        return base.length() > 16 ? base.substring(0, 16) : base;
    }
}
