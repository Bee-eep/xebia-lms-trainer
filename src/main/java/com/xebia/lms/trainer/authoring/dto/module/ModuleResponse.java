/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.dto.module;

import com.xebia.lms.trainer.authoring.model.CourseModule;

import java.util.UUID;

/**
 * ModuleResponse - API response for a module.
 */
public record ModuleResponse(
        UUID moduleId,
        UUID courseId,
        String title,
        Integer sortOrder
) {

    /**
     * Maps CourseModule entity to ModuleResponse.
     */
    public static ModuleResponse from(CourseModule module) {
        return new ModuleResponse(
                module.getModuleId(),
                module.getCourse().getCourseId(),
                module.getTitle(),
                module.getSortOrder()
        );
    }
}