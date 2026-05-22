package com.skyblockexp.ezcountdown.display.title;

import com.skyblockexp.ezcountdown.display.Validator;
import org.bukkit.entity.Player;

/**
 * Validates that the title API is available at runtime.
 * Accepts either {@code Player.sendTitle(String...)} (Spigot/Paper) or the
 * Adventure {@code Player.showTitle(Title)} (Paper 1.18+).
 */
public class TitleValidator extends Validator {

    @Override
    public ValidationResult validate() {
        // Legacy String-based sendTitle — present on all supported servers (Paper + Spigot 1.18+)
        try {
            Player.class.getMethod("sendTitle", String.class, String.class, int.class, int.class, int.class);
            return ValidationResult.ok();
        } catch (NoSuchMethodException ignored) {
            // fall through
        }
        // Adventure showTitle — Paper 1.18+ (secondary check)
        try {
            Player.class.getMethod("showTitle", net.kyori.adventure.title.Title.class);
            return ValidationResult.ok();
        } catch (NoSuchMethodException | NoClassDefFoundError e) {
            return ValidationResult.fail("Title unsupported: neither Player.sendTitle nor Player.showTitle found.");
        }
    }
}
