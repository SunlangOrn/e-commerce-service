package com.liang.shared.api;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiResponse<Void>> validation(MethodArgumentNotValidException ex) {
    Map<String, String> fields = ex.getBindingResult().getFieldErrors().stream()
        .collect(java.util.stream.Collectors.toMap(
            FieldError::getField,
            error -> error.getDefaultMessage() == null ? "Invalid value" : error.getDefaultMessage(),
            (first, second) -> first));
    return ResponseEntity.badRequest()
        .body(ApiResponse.fail("Validation failed", "VALIDATION_ERROR", fields));
  }

  @ExceptionHandler(AuthenticationException.class)
  ResponseEntity<ApiResponse<Void>> authentication(AuthenticationException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiResponse.fail("Authentication required", "UNAUTHORIZED", ex.getMessage()));
  }

  @ExceptionHandler(AccessDeniedException.class)
  ResponseEntity<ApiResponse<Void>> accessDenied(AccessDeniedException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(ApiResponse.fail("Permission denied", "FORBIDDEN", ex.getMessage()));
  }

  @ExceptionHandler(NotFoundException.class)
  ResponseEntity<ApiResponse<Void>> notFound(NotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.fail(ex.getMessage(), "NOT_FOUND", null));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  ResponseEntity<ApiResponse<Void>> badRequest(IllegalArgumentException ex) {
    return ResponseEntity.badRequest()
        .body(ApiResponse.fail(ex.getMessage(), "BAD_REQUEST", null));
  }

  @ExceptionHandler(IllegalStateException.class)
  ResponseEntity<ApiResponse<Void>> conflict(IllegalStateException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ApiResponse.fail(ex.getMessage(), "CONFLICT", null));
  }
}
