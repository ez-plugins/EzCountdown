package com.skyblockexp.ezcountdown.display.chat;

import com.skyblockexp.ezcountdown.display.Validator;

/**
 * Chat is always available on supported Bukkit/Spigot servers; validator
 * currently always returns success but exists for symmetry and future checks.
 */
public class ChatValidator extends Validator {

    @Override
    public ValidationResult validate() {
        return ValidationResult.ok();
    }
}
