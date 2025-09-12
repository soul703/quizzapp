package com.example.quizzapp.application.ports.out;

import com.example.quizzapp.domain.user.User;

import java.util.Optional;

/**
 * An output port for validating a token and retrieving the authenticated user's data.
 */
public interface AuthenticatedUserProvider {
    
    /**
     * Validates the given token and returns the corresponding User.
     *
     * @param token The authentication token.
     * @return An Optional containing the User if the token is valid and the user exists,
     *         otherwise an empty Optional.
     */
    Optional<User> getUserByToken(String token);
}