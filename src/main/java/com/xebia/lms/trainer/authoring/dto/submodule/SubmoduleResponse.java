/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.dto.submodule;

import com.xebia.lms.trainer.authoring.model.Submodule;

import java.util.UUID;

/**
 * SubmoduleResponse - API response for a submodule.
 */
public record SubmoduleResponse(
        UUID submoduleId,
        UUID moduleId,
        String title,
        Integer sortOrder,
        Integer estMinutes
) {

    /**
     * Maps Submodule entity to SubmoduleResponse.
     */
    public static SubmoduleResponse from(Submodule submodule) {
        return new SubmoduleResponse(
                submodule.getSubmoduleId(),
                submodule.getModule().getModuleId(),
                submodule.getTitle(),
                submodule.getSortOrder(),
                submodule.getEstMinutes()
        );
    }
}