package com.skyblockexp.ezcountdown.display.scoreboard;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.display.DisplayHandler;
import com.skyblockexp.ezcountdown.display.StackableDisplay;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScoreboardDisplay implements StackableDisplay {

    // Track per-player batch objective names so we can clear them later
    private final Map<UUID, String> batchObjectiveNames = new ConcurrentHashMap<>();

    @Override
    public void display(Countdown countdown, String message, long remainingSeconds) {
        // If timer reached zero, remove any displayed objective for this countdown
        if (remainingSeconds <= 0L) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                try {
                    Scoreboard scoreboard = player.getScoreboard();
                    if (scoreboard == null) continue;
                    String objectiveName = buildObjectiveName(countdown.getName());
                    Objective objective = scoreboard.getObjective(objectiveName);
                    if (objective != null) objective.unregister();
                } catch (NoClassDefFoundError | UnsupportedOperationException | IllegalArgumentException ignored) {
                    // ignore
                }
            }
            return;
        }

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
        // Remove any batch objectives we created for players
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                Scoreboard scoreboard = player.getScoreboard();
                if (scoreboard == null) continue;
                String name = batchObjectiveNames.remove(player.getUniqueId());
                if (name != null) {
                    Objective obj = scoreboard.getObjective(name);
                    if (obj != null) obj.unregister();
                }
            } catch (NoClassDefFoundError | UnsupportedOperationException | IllegalArgumentException ignored) {
                // ignore
            }
        }
    }

    @Override
    public void displayMultiple(Collection<com.skyblockexp.ezcountdown.api.model.Countdown> countdowns, Map<com.skyblockexp.ezcountdown.api.model.Countdown, String> messages, Map<com.skyblockexp.ezcountdown.api.model.Countdown, Long> remaining) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;

        // Track batch objective names per-player so clearAll can remove them later

        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                // Build list of messages visible to this player
                java.util.List<String> visible = new java.util.ArrayList<>();
                for (com.skyblockexp.ezcountdown.api.model.Countdown c : countdowns) {
                    long rem = remaining.getOrDefault(c, 0L);
                    if (rem <= 0L) continue; // skip timers at zero
                    String perm = c.getVisibilityPermission();
                    if (perm == null || perm.isBlank() || player.hasPermission(perm)) {
                        String msg = messages.get(c);
                        if (msg != null) visible.add(msg);
                    }
                }

                // If nothing to show, remove any existing batch objective for this player
                if (visible.isEmpty()) {
                    String existing = this.batchObjectiveNames.remove(player.getUniqueId());
                    if (existing != null) {
                        Scoreboard scoreboard = player.getScoreboard();
                        if (scoreboard != null) {
                            Objective obj = scoreboard.getObjective(existing);
                            if (obj != null) obj.unregister();
                        }
                    }
                    continue;
                }

                Scoreboard scoreboard = player.getScoreboard();
                if (scoreboard == manager.getMainScoreboard()) {
                    scoreboard = manager.getNewScoreboard();
                    player.setScoreboard(scoreboard);
                }

                String objectiveName = buildBatchObjectiveName(player.getUniqueId());
                batchObjectiveNames.put(player.getUniqueId(), objectiveName);
                this.batchObjectiveNames.put(player.getUniqueId(), objectiveName);

                Objective objective = scoreboard.getObjective(objectiveName);
                if (objective == null) {
                    objective = scoreboard.registerNewObjective(objectiveName, "dummy", ChatColor.AQUA + "Countdowns");
                }
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                scoreboard.getEntries().forEach(scoreboard::resetScores);

                // Add entries (higher score displays higher on sidebar)
                int score = visible.size();
                for (String msg : visible) {
                    try {
                        objective.getScore(msg).setScore(score--);
                    } catch (NoClassDefFoundError | UnsupportedOperationException | IllegalArgumentException e) {
                        player.sendMessage(msg);
                    }
                }
            } catch (NoClassDefFoundError | UnsupportedOperationException | IllegalArgumentException e) {
                // fallback to chat if scoreboard ops fail — mirror the same guards used in the happy path
                for (com.skyblockexp.ezcountdown.api.model.Countdown c : countdowns) {
                    long rem = remaining.getOrDefault(c, 0L);
                    if (rem <= 0L) continue; // skip countdown whose timer has reached zero
                    if (!c.getDisplayTypes().contains(com.skyblockexp.ezcountdown.display.DisplayType.SCOREBOARD)) continue;
                    String perm = c.getVisibilityPermission();
                    if (perm == null || perm.isBlank() || player.hasPermission(perm)) {
                        String msg = messages.get(c);
                        if (msg != null) player.sendMessage(msg);
                    }
                }
            }
        }
    }

    private String buildBatchObjectiveName(UUID uid) {
        String hex = uid.toString().replaceAll("-", "");
        String base = "ezcd_b_" + (hex.length() > 8 ? hex.substring(0, 8) : hex);
        return base.length() > 16 ? base.substring(0, 16) : base;
    }

    private String buildObjectiveName(String name) {
        String normalized = name.toLowerCase(Locale.ROOT);
        String base = "ezcd_" + normalized.replaceAll("[^a-z0-9]", "");
        return base.length() > 16 ? base.substring(0, 16) : base;
    }
}
