/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.controller;

import com.xebia.lms.trainer.authoring.dto.CourseForm;
import com.xebia.lms.trainer.authoring.dto.CourseResponse;
import com.xebia.lms.trainer.authoring.model.Course;
import com.xebia.lms.trainer.authoring.service.AuthoringService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * CourseController - course metadata and publishing routes.
 * Required RBAC scope for every endpoint here: TRN:COURSE:MANAGE
 * (enforced once Identity/RBAC is wired in Phase-2; see SecurityConfig).
 */
@RestController
@RequestMapping("/api/v1/trainer/courses")
public class CourseController {

    private final AuthoringService authoringService;

    public CourseController(AuthoringService authoringService) {
        this.authoringService = authoringService;
    }

    /**
     * createCourse - POST /api/v1/trainer/courses
     * Creates a new DRAFT course for the calling trainer.
     *
     * X-Trainer-Id stands in for the trainer id that will come from the
     * verified JWT once Identity/RBAC is wired (Phase-2); using a header
     * for now keeps this module runnable standalone against Postman.
     */
    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(
            @RequestHeader("X-Trainer-Id") UUID trainerId,
            @Valid @RequestBody CourseForm form) {
        Course course = authoringService.createCourse(trainerId, form);
        return ResponseEntity.status(HttpStatus.CREATED).body(CourseResponse.from(course));
    }

    /**
     * publish - POST /api/v1/trainer/courses/{id}/publish
     * Freezes the course's current DRAFT content so learners see a stable
     * version. Rejected with 409 if the course isn't DRAFT.
     */
    @PostMapping("/{id}/publish")
    public ResponseEntity<CourseResponse> publish(@PathVariable("id") UUID courseId) {
        Course published = authoringService.publish(courseId);
        return ResponseEntity.ok(CourseResponse.from(published));
    }
}
