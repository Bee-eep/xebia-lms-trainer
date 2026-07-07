/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.repository;

import com.xebia.lms.trainer.authoring.model.Category;
import com.xebia.lms.trainer.authoring.model.Course;
import com.xebia.lms.trainer.authoring.model.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * CourseRepository - Spring Data generates the implementation at startup.
 */
public interface CourseRepository extends JpaRepository<Course, UUID> {

    /**
     * Returns all courses belonging to a category.
     */
    List<Course> findByCategory(Category category);

    /**
     * Returns all courses belonging to a category id.
     */
    List<Course> findByCategory_CategoryId(UUID categoryId);

    /**
     * Returns all courses of a trainer.
     */
    List<Course> findByTrainerId(UUID trainerId);

    /**
     * Returns all courses of a trainer filtered by status.
     */
    List<Course> findByTrainerIdAndStatus(UUID trainerId, CourseStatus status);

    /**
     * Checks duplicate course title inside a category.
     */
    boolean existsByCategory_CategoryIdAndTitleIgnoreCase(UUID categoryId, String title);

    /**
     * Checks whether a category contains any courses.
     */
    boolean existsByCategory_CategoryId(UUID categoryId);
}