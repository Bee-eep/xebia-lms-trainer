/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.dto;

import com.xebia.lms.trainer.authoring.model.Content;
import java.util.UUID;

/**
 * ContentDetailResponse - detailed content representation for the course detail tree.
 * Includes body and language fields which are excluded from standard list responses.
 */
public record ContentDetailResponse(
        UUID contentId,
        UUID submoduleId,
        String type,
        String body,
        String s3Key,
        String language,
        int sortOrder
) {
    public static ContentDetailResponse from(Content content) {
        return new ContentDetailResponse(
                content.getContentId(),
                content.getSubmoduleId(),
                content.getType().name(),
                content.getBody(),
                content.getS3Key(),
                content.getLanguage(),
                content.getSortOrder()
        );
    }
}
