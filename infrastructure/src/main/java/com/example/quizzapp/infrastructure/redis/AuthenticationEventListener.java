package com.example.quizzapp.infrastructure.redis;

import com.example.quizzapp.application.events.AuthenticationFailureEvent;
import com.example.quizzapp.application.events.AuthenticationSuccessEvent;
import com.example.quizzapp.domain.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.example.quizzapp.common.events.EventEnvelope; // Import mới

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationEventListener {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleAuthEvent(String message) {
        try {
            // Bước 1: Deserialize message thành "phong bì"
            EventEnvelope envelope = objectMapper.readValue(message, EventEnvelope.class);

            String eventType = envelope.getEventType();

            // Bước 2: Dựa vào eventType để deserialize payload một cách tường minh
            if (AuthenticationSuccessEvent.class.getName().equals(eventType)) {
                AuthenticationSuccessEvent event = objectMapper.treeToValue(envelope.getPayload(), AuthenticationSuccessEvent.class);
                handleSuccessEvent(event);
            } else if (AuthenticationFailureEvent.class.getName().equals(eventType)) {
                AuthenticationFailureEvent event = objectMapper.treeToValue(envelope.getPayload(), AuthenticationFailureEvent.class);
                handleFailureEvent(event);
            } else {
                log.warn("Received unknown event type from Redis channel: {}", eventType);
            }

        } catch (Exception e) {
            log.error("Failed to process event from Redis: {}", message, e);
        }
    }

    private void handleSuccessEvent(AuthenticationSuccessEvent event) {
        // ... logic giữ nguyên ...
        log.info("Handling successful authentication event from Redis for: {}", event.email());
        userRepository.findByEmail(event.email()).ifPresent(user -> {
            user.handleSuccessfulLogin();
            userRepository.save(user);
        });
    }

    private void handleFailureEvent(AuthenticationFailureEvent event) {
        log.warn("Handling failed authentication event from Redis for: {}", event.email());
        userRepository.findByEmail(event.email()).ifPresent(user -> {
            user.handleFailedLoginAttempt();
            log.info("User {} failed login attempts: {}", user.getEmail(), user.getPasswordFailedCount());
            userRepository.save(user);
        });
    }
}