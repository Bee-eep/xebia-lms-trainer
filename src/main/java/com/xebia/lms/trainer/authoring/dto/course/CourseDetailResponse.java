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
 * CourseDetailResponse - full representation of a course, its metadata, and nested sections/lessons/content blocks.
 */
public record CourseDetailResponse(
        UUID courseId,
        UUID trainerId,
        UUID domainId,
        String title,
        String summary,
        String level,
        String status,
        int version,
        Instant createdAt,
        List<ModuleDetailResponse> modules
) {
    public static CourseDetailResponse from(Course course, List<ModuleDetailResponse> modules) {
        return new CourseDetailResponse(
                course.getCourseId(),
                course.getTrainerId(),
                course.getDomainId(),
                course.getTitle(),
                course.getSummary(),
                course.getLevel().name(),
                course.getStatus().name(),
                course.getVersion(),
                course.getCreatedAt(),
                modules
        );
    }
}
