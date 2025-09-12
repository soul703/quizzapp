package com.example.quizzapp.application.ports.in;

import com.example.quizzapp.domain.user.User;

import java.util.Optional;

/**
 * An input port for retrieving the currently authenticated user.
 */
public interface GetCurrentUserPort {
    /**
     * Gets the domain User object for the currently authenticated principal.
     *
     * @return An Optional containing the User, or an empty Optional if no user is authenticated.
     */
    Optional<User> getCurrentUser();
}