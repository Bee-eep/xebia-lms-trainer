/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * CourseModule - a top-level module within a course.
 *
 * Hierarchy:
 * Category
 *   └── Course
 *        └── Module
 *             └── SubModule
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @OneToMany(mappedBy = "module",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Submodule> submodules;
}