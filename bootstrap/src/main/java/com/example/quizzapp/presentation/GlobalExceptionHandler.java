package com.example.quizzapp.presentation;

import com.example.quizzapp.common.exceptions.*;
import com.example.quizzapp.common.response.ApiError;
import com.example.quizzapp.common.response.ApiResponse;
import com.example.quizzapp.common.response.ValidationErrorDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // === 400 BAD REQUEST ===
    // Xử lý lỗi validation từ @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ValidationErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ValidationErrorDetail(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        ApiError apiError = new ApiError("VALIDATION_ERROR", details);
        ApiResponse<Void> response = ApiResponse.fail(apiError, "Validation failed. Please check your input.");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // === 401 UNAUTHORIZED ===
    // Xử lý tất cả các lỗi xác thực chung (sai mật khẩu, token không hợp lệ/hết hạn)
    @ExceptionHandler({AuthenticationFailedException.class, InvalidTokenException.class})
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationExceptions(RuntimeException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        ApiError apiError = new ApiError("AUTHENTICATION_FAILED", ex.getMessage());
        ApiResponse<Void> response = ApiResponse.fail(apiError, "Authentication failed. Please check your credentials or token.");
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // === 404 NOT FOUND ===
    // Xử lý lỗi không tìm thấy tài nguyên
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        ApiError apiError = new ApiError("RESOURCE_NOT_FOUND", ex.getMessage());
        ApiResponse<Void> response = ApiResponse.fail(apiError, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // === 409 CONFLICT ===
    // Xử lý các lỗi xung đột nghiệp vụ (email đã tồn tại, nickname đã tồn tại, etc.)
    @ExceptionHandler(BusinessConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessConflict(BusinessConflictException ex) {
        log.warn("Business conflict: {}", ex.getMessage());
        ApiError apiError = new ApiError("BUSINESS_CONFLICT", ex.getMessage());
        ApiResponse<Void> response = ApiResponse.fail(apiError, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // === 500 INTERNAL SERVER ERROR ===
    // Handler "bắt tất cả" cho các lỗi không lường trước
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAllExceptions(Exception ex) {
        log.error("An unhandled exception occurred: ", ex); // Ghi log đầy đủ stack trace
        ApiError apiError = new ApiError("INTERNAL_SERVER_ERROR", "An unexpected error occurred on the server.");
        ApiResponse<Void> response = ApiResponse.fail(apiError, "An unexpected error occurred.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccountLockedException(AccountLockedException ex) {
        log.warn("Access denied for locked account: {}", ex.getMessage());
        ApiError apiError = new ApiError("ACCOUNT_LOCKED", ex.getMessage());
        ApiResponse<Void> response = ApiResponse.fail(apiError, "Access to this resource is forbidden.");
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFoundException(UserNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage());
        ApiError apiError = new ApiError("USER_NOT_FOUND", ex.getMessage());
        ApiResponse<Void> response = ApiResponse.fail(apiError, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}