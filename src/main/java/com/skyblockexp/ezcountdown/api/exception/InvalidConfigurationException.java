package com.skyblockexp.ezcountdown.api.exception;

/**
 * Thrown when a {@link com.skyblockexp.ezcountdown.api.model.NotificationBuilder}
 * or {@link com.skyblockexp.ezcountdown.api.model.CountdownBuilder} is built
 * with invalid or missing configuration.
 *
 * <p>This replaces the plain {@link IllegalStateException} previously thrown
 * by builder {@code build()} methods so callers can catch a specific type.
 */
public class InvalidConfigurationException extends EzCountdownException {

    public InvalidConfigurationException(String message) {
        super(message);
    }

    public InvalidConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
