package com.skyblockexp.ezcountdown.display.dialog;

import com.skyblockexp.ezcountdown.display.Validator;

/**
 * Validates whether the Paper Dialog API (introduced in Paper 1.21.7) is
 * available on the current server runtime.
 *
 * <p>The check is performed entirely via reflection so that the plugin can
 * still load on Paper builds that predate the dialog API — the display type
 * will simply be skipped with a warning.
 */
public class DialogDisplayValidator extends Validator {

    @Override
    public ValidationResult validate() {
        try {
            Class.forName("io.papermc.paper.dialog.Dialog");
            // Also check that Audience#showDialog exists (requires Adventure 4.22+)
            Class.forName("net.kyori.adventure.dialog.DialogLike");
            return ValidationResult.ok();
        } catch (ClassNotFoundException e) {
            return ValidationResult.fail(
                    "Dialog display requires Paper 1.21.7+ with the Dialog API (io.papermc.paper.dialog.Dialog not found).");
        }
    }
}
