/*
 * Author : Garv
 */
package com.xebia.lms.trainer.common.exception;

/**
 * InvalidCourseStateException - thrown when an action is attempted against
 * a course that is not in the right lifecycle state for it, e.g. adding a
 * module to an already-PUBLISHED course, or publishing a course twice.
 * Mapped to HTTP 409 (Conflict) by GlobalExceptionHandler.
 */
public class InvalidCourseStateException extends RuntimeException {

    public InvalidCourseStateException(String message) {
        super(message);
    }
}
