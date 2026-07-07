/*
 * Author : Himanshu Verma
 */
package com.xebia.lms.trainer.batch.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Learner - represents a student in a batch.
 */
@Entity
@Table(name = "learners")
@Getter
@Setter
@NoArgsConstructor
public class Learner {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "learner_id")
    private UUID learnerId;

    @Column(name = "batch_id", nullable = false)
    private UUID batchId;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
