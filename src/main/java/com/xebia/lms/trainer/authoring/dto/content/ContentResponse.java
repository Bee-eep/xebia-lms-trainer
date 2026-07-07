/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.dto.content;

import com.xebia.lms.trainer.authoring.model.Content;

import java.util.UUID;

/**
 * ContentResponse - what the API returns after a content block is added
 * to a submodule.
 */
public record ContentResponse(
        UUID contentId,
        UUID submoduleId,
        String type,
        String s3Key,
        int sortOrder
) {

    /**
     * from - maps a Content entity to its API-facing representation. body
     * is intentionally left out for large TEXT/CODE blocks in list views;
     * this response is meant for "block created" acknowledgements.
     */
    public static ContentResponse from(Content content) {
        return new ContentResponse(
                content.getContentId(),
                content.getSubmoduleId(),
                content.getType().name(),
                content.getS3Key(),
                content.getSortOrder()
        );
    }
}
