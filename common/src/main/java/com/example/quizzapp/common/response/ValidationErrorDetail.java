package com.example.quizzapp.common.response;

/**
 * A simple record to hold details about a single validation error.
 *
 * @param field The name of the field that failed validation. Can be null for global errors.
 * @param message The user-friendly error message for this field.
 */
public record ValidationErrorDetail(String field, String message) {
}