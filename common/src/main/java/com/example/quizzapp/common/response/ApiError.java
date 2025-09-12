package com.example.quizzapp.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.List;

/**
 * Represents the structured error object within an ApiResponse.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    
    /**
     * A unique, machine-readable error code (e.g., "VALIDATION_ERROR", "USER_NOT_FOUND").
     */
    private final String code;
    
    /**
     * A list of specific error details, particularly useful for validation errors.
     * This field will be null if there are no specific details.
     */
    private final List<ValidationErrorDetail> details;

    /**
     * Constructor for errors with detailed validation information.
     */
    public ApiError(String code, List<ValidationErrorDetail> details) {
        this.code = code;
        this.details = details;
    }

    /**
     * Constructor for general errors without specific field details.
     */
    public ApiError(String code, String detailMessage) {
        this.code = code;
        // This provides a simple way to pass a single error message
        this.details = List.of(new ValidationErrorDetail(null, detailMessage));
    }

     /**
     * Constructor for general errors without any details.
     */
    public ApiError(String code) {
        this(code, (List<ValidationErrorDetail>) null);
    }
}