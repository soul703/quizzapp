// common/src/main/java/com/example/quizzapp/common/exceptions/UserNotFoundException.java
package com.example.quizzapp.common.exceptions;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}