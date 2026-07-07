/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.dto.course;

import com.xebia.lms.trainer.authoring.model.Course;

import java.time.Instant;
import java.util.UUID;

/**
 * CourseResponse - API response for a course.
 */
public record CourseResponse(
        UUID courseId,
        UUID categoryId,
        String categoryName,
        String title,
        String summary,
        String level,
        String status,
        int version,
        Instant createdAt,
        Instant updatedAt
) {

    /**
     * Maps Course entity to CourseResponse.
     */
    public static CourseResponse from(Course course) {
        return new CourseResponse(
                course.getCourseId(),
                course.getCategory().getCategoryId(),
                course.getCategory().getName(),
                course.getTitle(),
                course.getSummary(),
                course.getLevel().name(),
                course.getStatus().name(),
                course.getVersion(),
                course.getCreatedAt(),
                course.getUpdatedAt()
        );
    }
}