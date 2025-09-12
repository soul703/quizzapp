package com.example.quizzapp.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

/**
 * A generic wrapper for all API responses in the application.
 * This standardizes the structure of responses for both success and failure cases.
 *
 * @param <T> The type of the data payload.
 */
@Getter
// This annotation ensures that fields with null values are not included in the JSON output.
// It keeps the response clean, especially for success responses where 'error' is null,
// and for error responses where 'data' is null.
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final ApiError error;

    /**
     * Private constructor for successful responses.
     */
    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.error = null;
    }

    /**
     * Private constructor for failure responses.
     */
    private ApiResponse(boolean success, String message, ApiError error) {
        this.success = success;
        this.message = message;
        this.data = null;
        this.error = error;
    }

    // --- Static Factory Methods for convenient response creation ---

    /**
     * Creates a successful response with a data payload and a custom message.
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * Creates a successful response with a data payload and a default message.
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operation was successful.");
    }

    /**
     * Creates a successful response with no data payload (e.g., for create or update operations).
     */
    public static <T> ApiResponse<T> success(String message) {
        // We use <T> here to avoid type warnings, even though data is null.
        return new ApiResponse<>(true, message, null);
    }

    /**
     * Creates a failure response with an error object and a custom message.
     */
    public static <T> ApiResponse<T> fail(ApiError error, String message) {
        return new ApiResponse<>(false, message, error);
    }
}