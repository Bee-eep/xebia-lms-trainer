/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.repository;

import com.xebia.lms.trainer.authoring.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * ContentRepository - Spring Data generates the implementation at startup.
 */
public interface ContentRepository extends JpaRepository<Content, UUID> {

    /**
     * findBySubmoduleIdOrderBySortOrder - returns a lesson's content blocks
     * already sorted for display.
     */
    List<Content> findBySubmoduleIdOrderBySortOrder(UUID submoduleId);
}
