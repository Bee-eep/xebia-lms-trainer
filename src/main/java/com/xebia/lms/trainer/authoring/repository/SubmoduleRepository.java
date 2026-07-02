/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.repository;

import com.xebia.lms.trainer.authoring.model.Submodule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * SubmoduleRepository - Spring Data generates the implementation at startup.
 */
public interface SubmoduleRepository extends JpaRepository<Submodule, UUID> {

    /**
     * findByModuleIdOrderBySortOrder - returns a module's lessons already
     * sorted for display.
     */
    List<Submodule> findByModuleIdOrderBySortOrder(UUID moduleId);
}
