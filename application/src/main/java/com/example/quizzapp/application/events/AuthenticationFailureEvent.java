package com.example.quizzapp.application.events;

import java.time.Instant;

public record AuthenticationFailureEvent(String email, String errorMessage, Instant timestamp) {}