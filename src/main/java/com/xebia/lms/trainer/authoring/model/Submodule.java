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
 * Submodule - a single lesson within a module. Maps 1:1 to the
 * `submodule` table. estMinutes is the trainer's estimate of how long the
 * lesson takes, shown to learners before they start it.
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

    @Column(name = "module_id", nullable = false)
    private UUID moduleId;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "est_minutes")
    private Integer estMinutes;
}
