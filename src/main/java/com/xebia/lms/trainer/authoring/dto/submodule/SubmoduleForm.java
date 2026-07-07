/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.dto.submodule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * SubmoduleForm - request payload for creating or updating a submodule.
 */
public record SubmoduleForm(

        @NotBlank(message = "title is required")
        @Size(max = 160, message = "title must be at most 160 characters")
        String title,

        @PositiveOrZero(message = "sortOrder must be zero or positive")
        Integer sortOrder,

        @PositiveOrZero(message = "estMinutes must be zero or positive")
        Integer estMinutes
) {
}