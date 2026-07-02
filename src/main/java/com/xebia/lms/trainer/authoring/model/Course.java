/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Course - a course owned and authored by a trainer. Maps 1:1 to the
 * `course` table. Lives in DRAFT while being authored; publish() moves it
 * to PUBLISHED and freezes it for learners.
 */
@Entity
@Table(name = "course")
@Getter
@Setter
@NoArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "course_id")
    private UUID courseId;

    @Column(name = "trainer_id", nullable = false)
    private UUID trainerId;

    @Column(name = "domain_id", nullable = false)
    private UUID domainId;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(columnDefinition = "text")
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private CourseLevel level;

    @Column(nullable = false)
    private int version = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private CourseStatus status = CourseStatus.DRAFT;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
