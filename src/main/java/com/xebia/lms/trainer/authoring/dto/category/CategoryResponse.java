/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.dto.category;

import com.xebia.lms.trainer.authoring.model.Category;

import java.time.Instant;
import java.util.UUID;

/**
 * CategoryResponse - API response for Category.
 */
public record CategoryResponse(
        UUID categoryId,
        String name,
        String icon,
        String color,
        String description,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {

    /**
     * Maps Category entity to CategoryResponse.
     */
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getCategoryId(),
                category.getName(),
                category.getIcon(),
                category.getColor(),
                category.getDescription(),
                category.isActive(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}