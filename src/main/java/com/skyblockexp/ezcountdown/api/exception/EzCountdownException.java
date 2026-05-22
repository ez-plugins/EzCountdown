package com.skyblockexp.ezcountdown.api.exception;

/**
 * Base unchecked exception for all EzCountdown API errors.
 *
 * <p>Catch this type to handle any error thrown by the EzCountdown API in one
 * place, or catch a more specific subclass for fine-grained handling.
 */
public class EzCountdownException extends RuntimeException {

    public EzCountdownException(String message) {
        super(message);
    }

    public EzCountdownException(String message, Throwable cause) {
        super(message, cause);
    }
}
