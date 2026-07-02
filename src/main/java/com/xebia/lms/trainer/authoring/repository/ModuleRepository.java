/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.repository;

import com.xebia.lms.trainer.authoring.model.CourseModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * ModuleRepository - Spring Data generates the implementation at startup.
 */
public interface ModuleRepository extends JpaRepository<CourseModule, UUID> {

    /**
     * findByCourseIdOrderBySortOrder - returns a course's modules already
     * sorted for display, so callers never have to sort them again.
     */
    List<CourseModule> findByCourseIdOrderBySortOrder(UUID courseId);
}
