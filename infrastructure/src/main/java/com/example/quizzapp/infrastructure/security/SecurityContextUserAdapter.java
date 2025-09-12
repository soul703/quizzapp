package com.example.quizzapp.infrastructure.security;

import com.example.quizzapp.application.ports.in.GetCurrentUserPort;
import com.example.quizzapp.domain.user.User;
import com.example.quizzapp.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityContextUserAdapter implements GetCurrentUserPort {

    private final UserRepository userRepository;

    @Override
    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            return Optional.empty();
        }

        UserDetails principal = (UserDetails) authentication.getPrincipal();
        String email = principal.getUsername();

        // Tải lại user từ repository để đảm bảo dữ liệu là mới nhất
        return userRepository.findByEmail(email);
    }
}