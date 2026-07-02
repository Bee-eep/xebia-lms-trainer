/*
 * Author : Garv
 */
package com.xebia.lms.trainer.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler - a single place that turns exceptions into
 * consistent JSON error bodies with the right HTTP status, so individual
 * controllers only ever throw a meaningful exception and never build error
 * responses themselves.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * handleNotFound - maps ResourceNotFoundException to 404. Triggered
     * whenever a service method can't find the course/module/submodule the
     * caller referenced by id.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body(ex.getMessage()));
    }

    /**
     * handleInvalidState - maps InvalidCourseStateException to 409 Conflict.
     * Triggered when an action doesn't fit the resource's current lifecycle
     * state, e.g. editing a course that is already PUBLISHED.
     */
    @ExceptionHandler(InvalidCourseStateException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidState(InvalidCourseStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body(ex.getMessage()));
    }

    /**
     * handleValidation - maps @Valid failures on request DTOs (CourseForm,
     * ModuleForm, etc.) to 400 Bad Request, and includes the first field
     * error message so the caller knows exactly what to fix.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + " " + err.getDefaultMessage())
                .orElse("validation failed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body(message));
    }

    /**
     * body - builds a small, consistent error payload shape:
     * { "timestamp": ..., "message": ... }
     */
    private Map<String, Object> body(String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now());
        body.put("message", message);
        return body;
    }
}
