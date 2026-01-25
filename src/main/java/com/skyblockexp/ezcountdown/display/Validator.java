package com.skyblockexp.ezcountdown.display;

/**
 * Base class for display validators. Implementations should return a
 * {@link ValidationResult} indicating whether the target display is usable
 * on the current runtime/server environment.
 */
public abstract class Validator {

    public static final class ValidationResult {
        private final boolean valid;
        private final String message;

        private ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public static ValidationResult ok() {
            return new ValidationResult(true, "");
        }

        public static ValidationResult fail(String message) {
            return new ValidationResult(false, message);
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * Validate the environment for this display.
     *
     * @return a {@link ValidationResult} describing validity and an optional message
     */
    public abstract ValidationResult validate();
}
