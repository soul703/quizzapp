package com.example.quizzapp.application.ports.in;

public interface AuthenticationPort {
    void authenticate(String username, String password);
}