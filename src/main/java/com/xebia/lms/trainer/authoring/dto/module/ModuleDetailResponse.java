/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.dto.module;

import com.xebia.lms.trainer.authoring.dto.submodule.SubmoduleDetailResponse;
import com.xebia.lms.trainer.authoring.model.CourseModule;

import java.util.List;
import java.util.UUID;

/**
 * ModuleDetailResponse - complete representation of a module with its submodules.
 */
public record ModuleDetailResponse(
        UUID moduleId,
        UUID courseId,
        String title,
        Integer sortOrder,
        List<SubmoduleDetailResponse> submodules
) {

    public static ModuleDetailResponse from(
            CourseModule module,
            List<SubmoduleDetailResponse> submodules) {

        return new ModuleDetailResponse(
                module.getModuleId(),
                module.getCourse().getCourseId(),
                module.getTitle(),
                module.getSortOrder(),
                submodules
        );
    }
}