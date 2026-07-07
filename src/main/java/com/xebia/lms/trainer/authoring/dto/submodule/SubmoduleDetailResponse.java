/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.dto.submodule;

import com.xebia.lms.trainer.authoring.dto.content.ContentDetailResponse;
import com.xebia.lms.trainer.authoring.model.Submodule;
import java.util.List;
import java.util.UUID;

/**
 * SubmoduleDetailResponse - submodule representation containing its detailed content blocks.
 */
public record SubmoduleDetailResponse(
        UUID submoduleId,
        UUID moduleId,
        String title,
        int sortOrder,
        Integer estMinutes,
        List<ContentDetailResponse> contentBlocks
) {
    public static SubmoduleDetailResponse from(Submodule submodule, List<ContentDetailResponse> contentBlocks) {
        return new SubmoduleDetailResponse(
                submodule.getSubmoduleId(),
                submodule.getModuleId(),
                submodule.getTitle(),
                submodule.getSortOrder(),
                submodule.getEstMinutes(),
                contentBlocks
        );
    }
}
