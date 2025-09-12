package com.example.quizzapp.application.services;


import com.example.quizzapp.application.dtos.AuthenticatedUserDto;
import com.example.quizzapp.application.dtos.LoginCommand;
import com.example.quizzapp.application.dtos.LoginResult;
import com.example.quizzapp.application.dtos.RegisterUserCommand;
import com.example.quizzapp.application.events.AuthenticationFailureEvent;
import com.example.quizzapp.application.events.AuthenticationSuccessEvent;
import com.example.quizzapp.application.ports.in.AuthenticationPort;
import com.example.quizzapp.application.ports.out.AuthenticatedUserProvider;
import com.example.quizzapp.application.ports.out.AuthenticationEventPublisher;
import com.example.quizzapp.application.ports.out.TokenProvider;
import com.example.quizzapp.common.exceptions.AccountLockedException;
import com.example.quizzapp.common.exceptions.AuthenticationFailedException;
import com.example.quizzapp.common.exceptions.InvalidTokenException;
import com.example.quizzapp.domain.user.User;
import com.example.quizzapp.domain.user.UserRepository;
import com.example.quizzapp.domain.user.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Import từ infrastructure
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationPort authenticationPort; // Sửa ở đây
    private final TokenProvider tokenProvider;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final AuthenticationEventPublisher eventPublisher;


    @Transactional
    public User registerUser(RegisterUserCommand command) {
        if (userRepository.existsByEmail(command.email().toLowerCase())) {
            throw new IllegalStateException("Email already in use");
        }
        String hashedPassword = passwordEncoder.encode(command.password());
        User newUser = User.register(command.email(), hashedPassword, command.role());
        log.info("New User: {}", newUser.getPasswordHash());
        return userRepository.save(newUser);
    }
    // ..

    @Transactional(readOnly = true) // This transaction is read-only as it doesn't write to the DB.
    public LoginResult login(LoginCommand command) {
        try {
            // 1. Delegate authentication to the dedicated port.
            // The adapter behind this port will handle password matching and account status checks (e.g., locked).
            authenticationPort.authenticate(command.email(), command.password());

            // 2. If authentication is successful, find the user to generate a token.
            User user = userRepository.findByEmail(command.email())
                    .orElseThrow(() -> new IllegalStateException("User disappeared after successful authentication."));
            if (user.getStatus() == UserStatus.LOCKED) {
                throw new AccountLockedException("Account is locked due to multiple failed login attempts.");
            }

            // 3. Publish a success event. The listener will handle resetting the failed attempt counter.
            eventPublisher.publishSuccessEvent(new AuthenticationSuccessEvent(command.email(), Instant.now()));

            // 4. Generate the token.
            String token = tokenProvider.generateToken(user);

            // 5. Return the result.
            return new LoginResult(user.getId(), user.getEmail(), user.getRole(), token);

        } catch (AuthenticationFailedException e) {
            // 6. If authentication fails, publish a failure event.
            // The listener will handle incrementing the failed attempt counter.
            eventPublisher.publishFailureEvent(new AuthenticationFailureEvent(command.email(), e.getMessage(), Instant.now()));

            // 7. Re-throw the exception for the GlobalExceptionHandler to handle.
            throw e;
        }
    }
    public AuthenticatedUserDto verifyTokenAndGetUser(String token) {
        return authenticatedUserProvider.getUserByToken(token)
                .map(AuthenticatedUserDto::from)
                .orElseThrow(() -> new InvalidTokenException("Token is invalid or expired."));
    }
}