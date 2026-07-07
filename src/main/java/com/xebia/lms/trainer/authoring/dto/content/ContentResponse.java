/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.dto.content;

import com.xebia.lms.trainer.authoring.model.Content;

import java.util.UUID;

/**
 * ContentResponse - API response for a content block.
 */
public record ContentResponse(
        UUID contentId,
        UUID submoduleId,
        String type,
        String headingLevel,
        String body,
        String s3Key,
        String url,
        String language,
        String data,
        Integer sortOrder
) {

    /**
     * Maps Content entity to ContentResponse.
     */
    public static ContentResponse from(Content content) {
        return new ContentResponse(
                content.getContentId(),
                content.getSubmodule().getSubmoduleId(),
                content.getType().name(),
                content.getHeadingLevel(),
                content.getBody(),
                content.getS3Key(),
                content.getUrl(),
                content.getLanguage(),
                content.getData(),
                content.getSortOrder()
        );
    }
}