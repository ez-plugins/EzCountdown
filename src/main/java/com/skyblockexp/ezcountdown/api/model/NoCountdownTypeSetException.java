package com.skyblockexp.ezcountdown.api.model;

/** Thrown when a Countdown is being built without a CountdownType. */
public class NoCountdownTypeSetException extends IllegalStateException {
    public NoCountdownTypeSetException() {
        super("No CountdownType set for Countdown");
    }

    public NoCountdownTypeSetException(String message) {
        super(message);
    }

    public NoCountdownTypeSetException(String message, Throwable cause) {
        super(message, cause);
    }
}
