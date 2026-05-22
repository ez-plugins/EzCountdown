package com.skyblockexp.ezcountdown.display.title;

import com.skyblockexp.ezcountdown.display.Validator;
import org.bukkit.entity.Player;

/**
 * Validates that at least one title-capable API is available at runtime.
 * Accepts the Adventure {@code showTitle} (Paper 1.18+) or the legacy
 * {@code sendTitle(String...)} (Spigot).
 */
public class TitleValidator extends Validator {

    @Override
    public ValidationResult validate() {
        // Prefer Adventure showTitle — present on Paper 1.18+
        try {
            Player.class.getMethod("showTitle", net.kyori.adventure.title.Title.class);
            return ValidationResult.ok();
        } catch (NoSuchMethodException | NoClassDefFoundError ignored) {
            // fall through to legacy check
        }
        // Legacy String-based sendTitle — Spigot 1.8+
        try {
            Player.class.getMethod("sendTitle", String.class, String.class, int.class, int.class, int.class);
            return ValidationResult.ok();
        } catch (NoSuchMethodException e) {
            return ValidationResult.fail("Title unsupported: neither Player.showTitle nor Player.sendTitle found.");
        }
    }
}
