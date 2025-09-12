package com.example.quizzapp.application.ports.out;

import com.example.quizzapp.application.events.AuthenticationFailureEvent;
import com.example.quizzapp.application.events.AuthenticationSuccessEvent;

public interface AuthenticationEventPublisher {
    void publishSuccessEvent(AuthenticationSuccessEvent event);
    void publishFailureEvent(AuthenticationFailureEvent event);
}