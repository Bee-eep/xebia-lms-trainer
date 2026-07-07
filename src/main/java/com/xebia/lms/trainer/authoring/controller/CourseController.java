/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.controller;

import com.xebia.lms.trainer.authoring.dto.course.CourseDetailResponse;
import com.xebia.lms.trainer.authoring.dto.course.CourseForm;
import com.xebia.lms.trainer.authoring.dto.course.CourseResponse;
import com.xebia.lms.trainer.authoring.model.Course;
import com.xebia.lms.trainer.authoring.service.AuthoringService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * CourseController - course management routes.
 */
@RestController
@RequestMapping("/api/v1/trainer/courses")
public class CourseController {

    private final AuthoringService authoringService;

    public CourseController(AuthoringService authoringService) {
        this.authoringService = authoringService;
    }

    /**
     * Create Course under a Category.
     */
    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(
            @RequestHeader("X-Trainer-Id") UUID trainerId,
            @Valid @RequestBody CourseForm form) {

        Course course = authoringService.createCourse(trainerId, form);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CourseResponse.from(course));
    }

    /**
     * Get all courses.
     */
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {

        List<CourseResponse> responses = authoringService.getAllCourses()
                .stream()
                .map(CourseResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    /**
     * Get complete course hierarchy.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseDetailResponse> getCourseDetail(
            @PathVariable("id") UUID courseId) {

        return ResponseEntity.ok(authoringService.getCourseDetail(courseId));
    }

    /**
     * Update Course.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable("id") UUID courseId,
            @Valid @RequestBody CourseForm form) {

        Course updated = authoringService.updateCourse(courseId, form);
        return ResponseEntity.ok(CourseResponse.from(updated));
    }

    /**
     * Delete Course.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable("id") UUID courseId) {

        authoringService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Publish Course.
     */
    @PostMapping("/{id}/publish")
    public ResponseEntity<CourseResponse> publish(
            @PathVariable("id") UUID courseId) {

        Course published = authoringService.publish(courseId);
        return ResponseEntity.ok(CourseResponse.from(published));
    }

    /**
     * Get all courses by Category.
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<CourseResponse>> getCoursesByCategory(
            @PathVariable UUID categoryId) {

        List<CourseResponse> responses = authoringService.getCoursesByCategory(categoryId)
                .stream()
                .map(CourseResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }
}