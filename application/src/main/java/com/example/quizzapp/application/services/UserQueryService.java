package com.example.quizzapp.application.services;

import com.example.quizzapp.application.dtos.AuthenticatedUserDto;
 // Tạo exception này
import com.example.quizzapp.application.ports.in.GetCurrentUserPort;
import com.example.quizzapp.common.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final GetCurrentUserPort getCurrentUserPort;

    @Transactional(readOnly = true) // Tối ưu hóa cho truy vấn chỉ đọc
    public AuthenticatedUserDto findCurrentUser() {
        return getCurrentUserPort.getCurrentUser()
                .map(AuthenticatedUserDto::from)
                .orElseThrow(() -> new UserNotFoundException("No authenticated user found in the current context."));
    }
}