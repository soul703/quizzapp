package com.example.quizzapp.common.exceptions;

public class AccountLockedException extends  RuntimeException {
    public  AccountLockedException(String message) {
        super(message);
    }
}
