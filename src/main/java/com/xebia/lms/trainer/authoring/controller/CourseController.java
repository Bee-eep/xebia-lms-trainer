/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.controller;

import com.xebia.lms.trainer.authoring.dto.*;
import java.util.List;
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

    /**
     * getAllCourses - GET /api/v1/trainer/courses
     * Returns a list of all courses in the system.
     */
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        List<Course> courses = authoringService.getAllCourses();
        List<CourseResponse> responses = courses.stream()
                .map(CourseResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    /**
     * getCourseDetail - GET /api/v1/trainer/courses/{id}
     * Returns the full nested structure of a specific course.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseDetailResponse> getCourseDetail(@PathVariable("id") UUID courseId) {
        CourseDetailResponse response = authoringService.getCourseDetail(courseId);
        return ResponseEntity.ok(response);
    }

    /**
     * updateCourse - PUT /api/v1/trainer/courses/{id}
     * Updates the metadata of a specific DRAFT course.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable("id") UUID courseId,
            @Valid @RequestBody CourseForm form) {
        Course updated = authoringService.updateCourse(courseId, form);
        return ResponseEntity.ok(CourseResponse.from(updated));
    }

    /**
     * deleteCourse - DELETE /api/v1/trainer/courses/{id}
     * Deletes a specific DRAFT course and all its modules, submodules, and content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable("id") UUID courseId) {
        authoringService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }
}
