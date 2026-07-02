/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * CourseModule - a top-level section of a course (e.g. "Week 1"). Maps 1:1
 * to the `course_module` table. Ordered within its course by sortOrder.
 */
@Entity
@Table(name = "course_module")
@Getter
@Setter
@NoArgsConstructor
public class CourseModule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "module_id")
    private UUID moduleId;

    @Column(name = "course_id", nullable = false)
    private UUID courseId;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;
}
