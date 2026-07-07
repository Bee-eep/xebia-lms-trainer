/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.dto.module;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * ModuleForm - request payload for creating or updating a module.
 */
public record ModuleForm(

        @NotBlank(message = "title is required")
        @Size(max = 160, message = "title must be at most 160 characters")
        String title,

        @PositiveOrZero(message = "sortOrder must be zero or positive")
        Integer sortOrder
) {
}