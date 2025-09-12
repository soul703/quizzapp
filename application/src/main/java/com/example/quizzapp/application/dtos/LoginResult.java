package com.example.quizzapp.application.dtos;

import com.example.quizzapp.domain.user.UserRole;
import java.util.UUID;

/**
 * A Result Data Transfer Object representing the outcome of a successful login.
 * It contains the JWT access token and basic user information for the client.
 */
public record LoginResult(
    UUID userId,
    String email,
    UserRole role,
    String accessToken
) {
}