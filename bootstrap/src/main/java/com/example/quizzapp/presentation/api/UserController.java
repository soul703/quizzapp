package com.example.quizzapp.presentation.api;

import com.example.quizzapp.application.dtos.AuthenticatedUserDto;
import com.example.quizzapp.application.services.UserQueryService;

import com.example.quizzapp.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserQueryService userQueryService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()") // Đảm bảo chỉ người dùng đã đăng nhập mới gọi được
    public ResponseEntity<ApiResponse<AuthenticatedUserDto>> getCurrentUser() {
        AuthenticatedUserDto currentUser = userQueryService.findCurrentUser();
        ApiResponse<AuthenticatedUserDto> response = ApiResponse.success(currentUser, "User data retrieved successfully.");
        return ResponseEntity.ok(response);
    }
}