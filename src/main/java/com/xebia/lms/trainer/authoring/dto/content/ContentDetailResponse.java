/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.dto.content;

import com.xebia.lms.trainer.authoring.model.Content;

import java.util.UUID;

/**
 * ContentDetailResponse - detailed representation of a content block.
 */
public record ContentDetailResponse(
        UUID contentId,
        UUID submoduleId,
        String type,
        String headingLevel,
        String headingText,
        String body,
        String s3Key,
        String url,
        String language,
        String data,
        Integer sortOrder
) {

    public static ContentDetailResponse from(Content content) {
        return new ContentDetailResponse(
                content.getContentId(),
                content.getSubmodule().getSubmoduleId(),
                content.getType().name(),
                content.getHeadingLevel(),
                content.getHeadingText(),
                content.getBody(),
                content.getS3Key(),
                content.getUrl(),
                content.getLanguage(),
                content.getData(),
                content.getSortOrder()
        );
    }
}