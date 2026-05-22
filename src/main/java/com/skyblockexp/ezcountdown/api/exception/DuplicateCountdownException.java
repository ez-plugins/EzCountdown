package com.skyblockexp.ezcountdown.api.exception;

/**
 * Thrown when attempting to create a countdown whose name already exists.
 *
 * <p>Countdown names must be unique. Call {@link com.skyblockexp.ezcountdown.api.EzCountdownApi#deleteCountdown}
 * first if you need to replace an existing countdown.
 */
public class DuplicateCountdownException extends EzCountdownException {

    private final String countdownName;

    public DuplicateCountdownException(String name) {
        super("A countdown named '" + name + "' already exists");
        this.countdownName = name;
    }

    /** Returns the duplicate countdown name. */
    public String getCountdownName() {
        return countdownName;
    }
}
