/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.dto.course;

import com.xebia.lms.trainer.authoring.dto.module.ModuleDetailResponse;
import com.xebia.lms.trainer.authoring.model.Course;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * CourseDetailResponse - complete representation of a course and its
 * hierarchy (Category -> Course -> Module -> SubModule -> Content).
 */
public record CourseDetailResponse(
        UUID courseId,
        UUID trainerId,
        UUID categoryId,
        String categoryName,
        String title,
        String summary,
        String level,
        String status,
        int version,
        Instant createdAt,
        Instant updatedAt,
        List<ModuleDetailResponse> modules
) {

    public static CourseDetailResponse from(
            Course course,
            List<ModuleDetailResponse> modules) {

        return new CourseDetailResponse(
                course.getCourseId(),
                course.getTrainerId(),
                course.getCategory().getCategoryId(),
                course.getCategory().getName(),
                course.getTitle(),
                course.getSummary(),
                course.getLevel().name(),
                course.getStatus().name(),
                course.getVersion(),
                course.getCreatedAt(),
                course.getUpdatedAt(),
                modules
        );
    }
}