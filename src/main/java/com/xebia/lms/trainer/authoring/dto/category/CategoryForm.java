/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * CategoryForm - request payload for creating or updating a category.
 */
public record CategoryForm(

        @NotBlank(message = "Category name is required")
        @Size(max = 120, message = "Category name must not exceed 120 characters")
        String name,

        @Size(max = 2048, message = "Icon must not exceed 2048 characters")
        String icon,

        @Size(max = 16, message = "Color must not exceed 16 characters")
        String color,

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description
) {
}