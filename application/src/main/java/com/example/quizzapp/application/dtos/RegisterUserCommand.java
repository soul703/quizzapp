package com.example.quizzapp.application.dtos;

import com.example.quizzapp.domain.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


/**
 * A Command Data Transfer Object for registering a new user.
 * Uses Java Record for immutability and conciseness.
 * Includes Jakarta Validation annotations to define business rules for the input data.
 */
public record RegisterUserCommand(
    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Email should be valid.")
    String email,

    @NotBlank(message = "Password cannot be blank.")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters.")
    String password,
    @NotBlank(message = "First name cannot be blank.")
    @Size(max = 50, message = "First name cannot exceed 50 characters.")
    String firstName,
    @Size(max = 50, message = "Last name cannot exceed 50 characters.")
    @NotBlank(message = "Last name cannot be blank.")
    String lastName,
    @NotNull(message = "Role must be provided.")
    UserRole role
) {
}