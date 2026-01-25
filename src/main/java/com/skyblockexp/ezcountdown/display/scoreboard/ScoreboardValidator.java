package com.skyblockexp.ezcountdown.display.scoreboard;

import com.skyblockexp.ezcountdown.display.Validator;
import org.bukkit.Bukkit;

/**
 * Validate whether scoreboard manager is available at runtime.
 */
public class ScoreboardValidator extends Validator {

    @Override
    public ValidationResult validate() {
        try {
            if (Bukkit.getScoreboardManager() != null) {
                return ValidationResult.ok();
            }
            return ValidationResult.fail("Scoreboard manager is not available (Bukkit.getScoreboardManager() returned null).");
        } catch (NoClassDefFoundError | Exception e) {
            return ValidationResult.fail("Scoreboard unsupported: " + e.getMessage());
        }
    }
}
