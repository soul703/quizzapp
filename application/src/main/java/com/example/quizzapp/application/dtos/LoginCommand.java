package com.example.quizzapp.application.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * A Command Data Transfer Object for user login.
 */
public record LoginCommand(
    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Email should be valid.")
    String email,

    @NotBlank(message = "Password cannot be blank.")
    String password
) {
}