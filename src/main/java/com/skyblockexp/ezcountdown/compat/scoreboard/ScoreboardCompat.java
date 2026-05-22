package com.skyblockexp.ezcountdown.compat.scoreboard;

import net.kyori.adventure.text.Component;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Cross-version helpers for the Bukkit scoreboard API.
 *
 * <ul>
 *   <li>{@code registerObjective} — uses {@code Criteria.DUMMY} + Adventure {@link Component} on
 *       Paper 1.20.3+ where the legacy {@code (name, "dummy", String)} overload is deprecated, and
 *       falls back to the string overload on older builds or vanilla Spigot.
 *   <li>{@code clearObjective} — unregisters the objective to drop all stale score entries without
 *       calling the deprecated {@code Scoreboard.resetScores(String)}.
 * </ul>
 */
public final class ScoreboardCompat {

    /** {@code true} when {@code org.bukkit.scoreboard.Criteria} (Paper 1.20.3+) is available. */
    private static final boolean HAS_CRITERIA = detectCriteria();

    private ScoreboardCompat() {}

    private static boolean detectCriteria() {
        try {
            Class.forName("org.bukkit.scoreboard.Criteria");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    /**
     * Registers a new sidebar objective on {@code scoreboard} using the most modern overload
     * available at runtime.
     *
     * <p>On Paper 1.20.3+ this calls
     * {@code Scoreboard.registerNewObjective(name, Criteria.DUMMY, Component)} to avoid
     * the deprecation warning from the legacy string-criteria version. On older builds it
     * falls back to {@code registerNewObjective(name, "dummy", displayName)}.
     *
     * @param scoreboard  the scoreboard to register on
     * @param name        objective name (max 16 chars)
     * @param displayName human-readable display name
     * @return the newly registered {@link Objective}
     */
    @SuppressWarnings("deprecation")
    public static Objective registerObjective(Scoreboard scoreboard, String name, String displayName) {
        if (HAS_CRITERIA) {
            try {
                org.bukkit.scoreboard.Criteria dummy = org.bukkit.scoreboard.Criteria.DUMMY;
                if (dummy != null) {
                    return scoreboard.registerNewObjective(
                            name,
                            dummy,
                            net.kyori.adventure.text.Component.text(displayName));
                }
            } catch (NoSuchMethodError | NoClassDefFoundError ignored) {
                // Criteria class exists but overload mismatch — fall through to legacy
            }
        }
        return scoreboard.registerNewObjective(name, "dummy", displayName);
    }

    /**
     * Clears all score entries from {@code scoreboard} by unregistering the given objective and
     * returning the caller a fresh replacement — this avoids calling the deprecated
     * {@code Scoreboard.resetScores(String)}.
     *
     * <p>Usage:
     * <pre>{@code
     *   objective = ScoreboardCompat.resetObjective(scoreboard, objective, displayName);
     *   objective.setDisplaySlot(DisplaySlot.SIDEBAR);
     * }</pre>
     *
     * @param scoreboard  the scoreboard that owns the objective
     * @param objective   the objective to clear; unregistered on return
     * @param displayName display name for the re-registered objective
     * @return a freshly registered objective with the same name and display slot
     */
    public static Objective resetObjective(Scoreboard scoreboard, Objective objective, String displayName) {
        String name = objective.getName();
        objective.unregister();
        return registerObjective(scoreboard, name, displayName);
    }
}
