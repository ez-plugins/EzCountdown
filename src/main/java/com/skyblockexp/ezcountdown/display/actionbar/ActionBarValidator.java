package com.skyblockexp.ezcountdown.display.actionbar;

import com.skyblockexp.ezcountdown.display.Validator;
import org.bukkit.entity.Player;

/**
 * Validate whether action bar can be sent on this server/runtime.
 */
public class ActionBarValidator extends Validator {

    @Override
    public ValidationResult validate() {
        try {
            Player.class.getMethod("sendActionBar", String.class);
            return ValidationResult.ok();
        } catch (NoSuchMethodException e) {
            try {
                // Fallback to Spigot API availability
                Class.forName("net.md_5.bungee.api.ChatMessageType");
                return ValidationResult.ok();
            } catch (ClassNotFoundException ex) {
                return ValidationResult.fail("ActionBar unsupported: no Player.sendActionBar and Spigot action bar classes not found.");
            }
        }
    }
}
