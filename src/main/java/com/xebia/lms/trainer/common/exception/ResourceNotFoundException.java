/*
 * Author : Garv
 */
package com.xebia.lms.trainer.common.exception;

/**
 * ResourceNotFoundException - thrown when a lookup by id (course, module,
 * submodule, content) finds nothing. Mapped to HTTP 404 by
 * GlobalExceptionHandler so controllers never have to know the status code.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
