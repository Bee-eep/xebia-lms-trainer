/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.dto.content;

import com.xebia.lms.trainer.authoring.model.ContentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * ContentForm - what a trainer submits to add a content block to a
 * submodule. Which fields matter depends on `type`: TEXT/CODE use `body`
 * (and CODE also uses `language`); IMAGE/PDF/VIDEO use `s3Key`, obtained
 * beforehand from the Content Media Service's presigned-upload flow.
 */
public record ContentForm(

        @NotNull(message = "type is required")
        ContentType type,

        String body,

        String s3Key,

        String language,

        @PositiveOrZero(message = "sortOrder must be zero or positive")
        int sortOrder
) {
}
