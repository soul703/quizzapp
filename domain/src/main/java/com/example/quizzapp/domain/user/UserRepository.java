package com.example.quizzapp.domain.user;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing User entities.
 * Provides methods for saving, finding, and checking existence of users by email or ID.
 */
public interface UserRepository {
    User save(User user);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findById(UUID userId);
}