/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.dto.course;

import com.xebia.lms.trainer.authoring.model.Course;

import java.time.Instant;
import java.util.UUID;

/**
 * CourseResponse - what the API returns for a course. A thin projection
 * of the Course entity, kept separate so internal columns (trainerId
 * scoping details, etc.) are only exposed deliberately, via `from`.
 */
public record CourseResponse(
        UUID courseId,
        String title,
        String level,
        String status,
        int version,
        Instant createdAt
) {

    /**
     * from - maps a Course entity to its API-facing representation.
     * Centralizing the mapping here means controllers and services never
     * hand-roll it differently in two places.
     */
    public static CourseResponse from(Course course) {
        return new CourseResponse(
                course.getCourseId(),
                course.getTitle(),
                course.getLevel().name(),
                course.getStatus().name(),
                course.getVersion(),
                course.getCreatedAt()
        );
    }
}
