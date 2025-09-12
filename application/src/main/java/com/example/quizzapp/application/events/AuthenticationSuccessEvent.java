package com.example.quizzapp.application.events;

import java.time.Instant;

// Chỉ là một POJO/Record đơn giản, không kế thừa gì cả
public record AuthenticationSuccessEvent(String email, Instant timestamp) {}

