/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.repository;

import com.xebia.lms.trainer.authoring.model.Course;
import com.xebia.lms.trainer.authoring.model.CourseModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * ModuleRepository - Spring Data generates the implementation at startup.
 */
public interface ModuleRepository extends JpaRepository<CourseModule, UUID> {

    /**
     * Returns all modules of a course ordered by sort order.
     */
    List<CourseModule> findByCourseOrderBySortOrder(Course course);

    /**
     * Returns all modules of a course ordered by sort order.
     */
    List<CourseModule> findByCourse_CourseIdOrderBySortOrder(UUID courseId);

    /**
     * Checks whether a course contains any modules.
     */
    boolean existsByCourse_CourseId(UUID courseId);

    /**
     * Deletes all modules belonging to a course.
     */
    void deleteByCourse_CourseId(UUID courseId);
}