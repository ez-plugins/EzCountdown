package com.skyblockexp.ezcountdown.display.title;

import com.skyblockexp.ezcountdown.display.Validator;
import org.bukkit.entity.Player;

/**
 * Validate whether title API is present (Player.sendTitle).
 */
public class TitleValidator extends Validator {

    @Override
    public ValidationResult validate() {
        try {
            Player.class.getMethod("sendTitle", String.class, String.class, int.class, int.class, int.class);
            return ValidationResult.ok();
        } catch (NoSuchMethodException e) {
            return ValidationResult.fail("Title unsupported: Player.sendTitle method not found.");
        }
    }
}
