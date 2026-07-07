/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.repository;

import com.xebia.lms.trainer.authoring.model.CourseModule;
import com.xebia.lms.trainer.authoring.model.Submodule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * SubmoduleRepository - Spring Data generates the implementation at startup.
 */
public interface SubmoduleRepository extends JpaRepository<Submodule, UUID> {

    /**
     * Returns all submodules of a module ordered by sort order.
     */
    List<Submodule> findByModuleOrderBySortOrder(CourseModule module);

    /**
     * Returns all submodules of a module ordered by sort order.
     */
    List<Submodule> findByModule_ModuleIdOrderBySortOrder(UUID moduleId);

    /**
     * Checks whether a module contains any submodules.
     */
    boolean existsByModule_ModuleId(UUID moduleId);

    /**
     * Deletes all submodules belonging to a module.
     */
    void deleteByModule_ModuleId(UUID moduleId);
}