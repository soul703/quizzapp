package com.example.quizzapp.infrastructure.security;

import com.example.quizzapp.application.ports.out.AuthenticatedUserProvider;
import com.example.quizzapp.domain.user.User;
import com.example.quizzapp.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAuthenticatedUserAdapter implements AuthenticatedUserProvider {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public Optional<User> getUserByToken(String token) {
        // 1. Kiểm tra token có hợp lệ về mặt cú pháp và chữ ký không
        if (!jwtTokenProvider.validateToken(token)) {
            return Optional.empty();
        }

        try {
            // 2. Lấy user ID từ token
            String userIdStr = jwtTokenProvider.getUserIdFromJWT(token);
            UUID userId = UUID.fromString(userIdStr);

            // 3. Quan trọng: Tải lại user từ database để đảm bảo user vẫn tồn tại và hợp lệ
            return userRepository.findById(userId);
        } catch (Exception e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }
}