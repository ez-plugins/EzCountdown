package com.skyblockexp.ezcountdown.display.bossbar;

import com.skyblockexp.ezcountdown.display.Validator;

/**
 * Validator for the boss bar display. Ensures the server runtime provides
 * the Bukkit boss bar API (available on Minecraft 1.9+ implementations).
 */
public class BossbarValidator extends Validator {

    @Override
    public ValidationResult validate() {
        if (BossBarSupport.isSupported()) {
            return ValidationResult.ok();
        }
        return ValidationResult.fail("BossBar is not supported on this server (requires org.bukkit.boss.BossBar / Minecraft 1.9+).");
    }
}
