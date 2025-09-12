package com.example.quizzapp.common.exceptions;

public class EmailAlreadyExistsException extends BusinessConflictException {
    public EmailAlreadyExistsException(String email) {
        super("Email '" + email + "' is already in use.");
    }
}