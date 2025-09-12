package com.example.quizzapp.application.ports.out;
import com.example.quizzapp.domain.user.User;
public interface TokenProvider {
    String generateToken(User user);
    boolean validateToken(String token);
}