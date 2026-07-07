/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.dto.module;

import com.xebia.lms.trainer.authoring.model.CourseModule;

import java.util.UUID;

/**
 * ModuleResponse - what the API returns after a module is added to a
 * course.
 */
public record ModuleResponse(
        UUID moduleId,
        UUID courseId,
        String title,
        int sortOrder
) {

    /**
     * from - maps a CourseModule entity to its API-facing representation.
     */
    public static ModuleResponse from(CourseModule module) {
        return new ModuleResponse(
                module.getModuleId(),
                module.getCourseId(),
                module.getTitle(),
                module.getSortOrder()
        );
    }
}
