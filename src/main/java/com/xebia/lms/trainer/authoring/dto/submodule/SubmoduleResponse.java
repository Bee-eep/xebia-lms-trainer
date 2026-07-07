/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.dto.submodule;

import com.xebia.lms.trainer.authoring.model.Submodule;

import java.util.UUID;

/**
 * SubmoduleResponse - what the API returns after a lesson is added to a
 * module.
 */
public record SubmoduleResponse(
        UUID submoduleId,
        UUID moduleId,
        String title,
        int sortOrder,
        Integer estMinutes
) {

    /**
     * from - maps a Submodule entity to its API-facing representation.
     */
    public static SubmoduleResponse from(Submodule submodule) {
        return new SubmoduleResponse(
                submodule.getSubmoduleId(),
                submodule.getModuleId(),
                submodule.getTitle(),
                submodule.getSortOrder(),
                submodule.getEstMinutes()
        );
    }
}
