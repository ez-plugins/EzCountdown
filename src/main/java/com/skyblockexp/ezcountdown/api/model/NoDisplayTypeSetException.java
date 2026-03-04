package com.skyblockexp.ezcountdown.api.model;

/** Thrown when a Countdown is being built without any DisplayType configured. */
public class NoDisplayTypeSetException extends IllegalStateException {
    public NoDisplayTypeSetException() {
        super("No display types configured for Countdown");
    }

    public NoDisplayTypeSetException(String message) {
        super(message);
    }

    public NoDisplayTypeSetException(String message, Throwable cause) {
        super(message, cause);
    }
}
