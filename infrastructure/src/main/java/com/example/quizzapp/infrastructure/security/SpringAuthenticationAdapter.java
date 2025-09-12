package com.example.quizzapp.infrastructure.security;

import com.example.quizzapp.application.ports.in.AuthenticationPort;
import com.example.quizzapp.common.exceptions.AuthenticationFailedException;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


    @Component
    @RequiredArgsConstructor
    public class SpringAuthenticationAdapter implements AuthenticationPort {

        private final AuthenticationManager authenticationManager;

        @Override
        public void authenticate(String username, String password) {
            try {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(username, password)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (BadCredentialsException ex) {
                // Bắt exception cụ thể của Spring Security...
                // ... và ném ra một exception nghiệp vụ chung của chúng ta.
                throw new AuthenticationFailedException("Invalid username or password.", ex);
            }
        }
    }
