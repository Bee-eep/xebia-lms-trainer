/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.dto;

import com.xebia.lms.trainer.authoring.model.CourseModule;
import java.util.List;
import java.util.UUID;

/**
 * ModuleDetailResponse - module representation containing its detailed submodules.
 */
public record ModuleDetailResponse(
        UUID moduleId,
        UUID courseId,
        String title,
        int sortOrder,
        List<SubmoduleDetailResponse> submodules
) {
    public static ModuleDetailResponse from(CourseModule module, List<SubmoduleDetailResponse> submodules) {
        return new ModuleDetailResponse(
                module.getModuleId(),
                module.getCourseId(),
                module.getTitle(),
                module.getSortOrder(),
                submodules
        );
    }
}
