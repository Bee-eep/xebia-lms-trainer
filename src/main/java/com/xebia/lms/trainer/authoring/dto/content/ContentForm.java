/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.dto.content;

import com.xebia.lms.trainer.authoring.model.ContentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * ContentForm - request payload for creating or updating a content block.
 * Supports all block types in the Content Editor.
 */
public record ContentForm(

        @NotNull(message = "type is required")
        ContentType type,

        @Size(max = 4, message = "headingLevel must be H1-H6")
        String headingLevel,

        @Size(max = 255, message = "headingText must not exceed 255 characters")
        String headingText,

        String body,

        String s3Key,

        @Size(max = 500, message = "url must not exceed 500 characters")
        String url,

        @Size(max = 24, message = "language must not exceed 24 characters")
        String language,

        /**
         * JSON payload for complex blocks:
         * BULLETS, ARROW_LIST, NUMBERED_LIST,
         * TABLE, COMPARISON, etc.
         */
        String data,

        @PositiveOrZero(message = "sortOrder must be zero or positive")
        Integer sortOrder
) {
}