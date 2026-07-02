/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * SubmoduleForm - what a trainer submits to add a lesson to a module.
 * estMinutes is optional; a trainer may add it later once the lesson's
 * content is fleshed out.
 */
public record SubmoduleForm(

        @NotBlank(message = "title is required")
        @Size(max = 160, message = "title must be at most 160 characters")
        String title,

        @PositiveOrZero(message = "sortOrder must be zero or positive")
        int sortOrder,

        Integer estMinutes
) {
}
