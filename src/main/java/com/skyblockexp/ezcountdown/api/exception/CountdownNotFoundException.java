package com.skyblockexp.ezcountdown.api.exception;

/**
 * Thrown when an operation references a countdown name that does not exist.
 *
 * <p>Example: calling {@code startCountdown("typo")} when no countdown
 * named {@code "typo"} has been created.
 */
public class CountdownNotFoundException extends EzCountdownException {

    private final String countdownName;

    public CountdownNotFoundException(String name) {
        super("Countdown not found: '" + name + "'");
        this.countdownName = name;
    }

    /** Returns the name of the countdown that was not found. */
    public String getCountdownName() {
        return countdownName;
    }
}
