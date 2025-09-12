package com.example.quizzapp.application.dtos;

import com.example.quizzapp.domain.user.User;
import com.example.quizzapp.domain.user.UserRole;
import java.util.UUID;

public record AuthenticatedUserDto(UUID id, String email, UserRole role) {
    public static AuthenticatedUserDto from(User user) {
        return new AuthenticatedUserDto(user.getId(), user.getEmail(), user.getRole());
    }
}