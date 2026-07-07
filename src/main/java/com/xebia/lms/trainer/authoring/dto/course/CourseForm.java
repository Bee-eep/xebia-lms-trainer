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
 * CourseForm - what a trainer submits to create a new course. Kept
 * separate from the Course entity so the API contract can't accidentally
 * expose or accept fields (like status or version) that only the server
 * should control.
 */
public record CourseForm(

        @NotNull(message = "domainId is required")
        UUID domainId,

        @NotBlank(message = "title is required")
        @Size(max = 160, message = "title must be at most 160 characters")
        String title,

        String summary,

        @NotNull(message = "level is required")
        CourseLevel level
) {
}
