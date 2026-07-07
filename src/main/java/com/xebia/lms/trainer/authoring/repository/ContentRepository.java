/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.repository;

import com.xebia.lms.trainer.authoring.model.Content;
import com.xebia.lms.trainer.authoring.model.Submodule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * ContentRepository - Spring Data generates the implementation at startup.
 */
public interface ContentRepository extends JpaRepository<Content, UUID> {

    /**
     * Returns all content blocks of a submodule ordered for rendering.
     */
    List<Content> findBySubmoduleOrderBySortOrder(Submodule submodule);

    /**
     * Returns all content blocks by submodule id ordered for rendering.
     */
    List<Content> findBySubmodule_SubmoduleIdOrderBySortOrder(UUID submoduleId);

    /**
     * Checks whether a submodule contains any content.
     */
    boolean existsBySubmodule_SubmoduleId(UUID submoduleId);

    /**
     * Deletes all content blocks belonging to a submodule.
     */
    void deleteBySubmodule_SubmoduleId(UUID submoduleId);
}