package com.example.quizzapp.presentation.api;


import com.example.quizzapp.application.dtos.AuthenticatedUserDto;
import com.example.quizzapp.application.dtos.LoginCommand;
import com.example.quizzapp.application.dtos.LoginResult;
import com.example.quizzapp.application.dtos.RegisterUserCommand;
import com.example.quizzapp.application.services.AuthService;


import com.example.quizzapp.common.exceptions.InvalidTokenException;
import com.example.quizzapp.common.response.ApiError;
import com.example.quizzapp.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication-related operations such as user registration and login.
 * This controller handles incoming HTTP requests, delegates business logic to the AuthService,
 * and returns standardized API responses.
 *
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> registerUser(@Valid @RequestBody RegisterUserCommand command) {
        authService.registerUser(command);
        ApiResponse<Void> response = ApiResponse.success("User registered successfully.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PostMapping("/login")
        public ResponseEntity<ApiResponse<LoginResult>> login(@Valid @RequestBody LoginCommand command) {
            LoginResult result = authService.login(command);
            ApiResponse<LoginResult> response = ApiResponse.success(result, "Login successful.");
            return ResponseEntity.ok(response);
        }


    @PostMapping("/verify-token")
    public ResponseEntity<ApiResponse<AuthenticatedUserDto>> verifyToken(@RequestHeader("Authorization") String authHeader) {

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            // Lỗi này có thể được xử lý bởi GlobalExceptionHandler
            throw new InvalidTokenException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        AuthenticatedUserDto userDto = authService.verifyTokenAndGetUser(token);

        ApiResponse<AuthenticatedUserDto> response = ApiResponse.success(userDto, "Token is valid.");
        return ResponseEntity.ok(response);
    }



}