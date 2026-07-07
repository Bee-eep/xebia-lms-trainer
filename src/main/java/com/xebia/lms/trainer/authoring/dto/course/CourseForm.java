/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.dto.course;

import com.xebia.lms.trainer.authoring.model.CourseLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * CourseForm - request payload for creating or updating a course.
 */
public record CourseForm(

        @NotNull(message = "categoryId is required")
        UUID categoryId,

        @NotBlank(message = "title is required")
        @Size(max = 160, message = "title must be at most 160 characters")
        String title,

        @Size(max = 5000, message = "summary must be at most 5000 characters")
        String summary,

        @NotNull(message = "level is required")
        CourseLevel level
) {
}