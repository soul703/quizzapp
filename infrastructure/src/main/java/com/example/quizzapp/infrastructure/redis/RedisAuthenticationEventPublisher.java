package com.example.quizzapp.infrastructure.redis;

import com.example.quizzapp.application.events.AuthenticationFailureEvent;
import com.example.quizzapp.application.events.AuthenticationSuccessEvent;
import com.example.quizzapp.application.ports.out.AuthenticationEventPublisher;
import com.example.quizzapp.common.events.EventEnvelope;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisAuthenticationEventPublisher implements AuthenticationEventPublisher {

    private static final String AUTH_EVENTS_CHANNEL = "authentication-events";
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publishSuccessEvent(AuthenticationSuccessEvent event) {
        publish(event);
    }

    @Override
    public void publishFailureEvent(AuthenticationFailureEvent event) {
        publish(event);
    }

    private void publish(Object event) {
        try {
            // Bước 1: Chuyển đổi payload sự kiện thành JsonNode
            JsonNode payload = objectMapper.valueToTree(event);

            // Bước 2: Tạo "phong bì" chứa loại sự kiện và payload
            EventEnvelope envelope = new EventEnvelope(event.getClass().getName(), payload);

            // Bước 3: Serialize "phong bì" thành chuỗi JSON
            String message = objectMapper.writeValueAsString(envelope);

            // Bước 4: Publish chuỗi JSON
            stringRedisTemplate.convertAndSend(AUTH_EVENTS_CHANNEL, message);

            log.info("Published event envelope: {}", message);
        } catch (Exception e) {
            log.error("Failed to publish event of type {}: {}", event.getClass().getName(), e.getMessage());
        }
    }
}