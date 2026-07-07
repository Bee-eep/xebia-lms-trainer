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
 * Submodule - a lesson within a module.
 *
 * Hierarchy:
 * Category
 *   └── Course
 *        └── Module
 *             └── SubModule
 *                  └── Content
 */
@Entity
@Table(name = "submodule")
@Getter
@Setter
@NoArgsConstructor
public class Submodule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "submodule_id")
    private UUID submoduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private CourseModule module;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "est_minutes")
    private Integer estMinutes;

    @OneToMany(mappedBy = "submodule",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    private List<Content> contents;
}