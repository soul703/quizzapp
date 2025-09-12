package com.example.quizzapp.common.exceptions;

/**
 * Thrown when user authentication fails due to incorrect credentials or other reasons.
 */
public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException(String message) {
        super(message);
    }

    public AuthenticationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}