/*
 * Author : Garv
 */
package com.xebia.lms.trainer.evaluation.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * TrainerEvaluation - a trainer's evaluation/score override of a learner submission.
 */
@Entity
@Table(name = "trainer_evaluation")
@Getter
@Setter
@NoArgsConstructor
public class TrainerEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "evaluation_id")
    private UUID evaluationId;

    @Column(name = "trainer_id", nullable = false)
    private UUID trainerId;

    @Column(name = "learner_id", nullable = false)
    private UUID learnerId;

    @Column(name = "result_id", nullable = false)
    private UUID resultId;

    @Column(name = "override_score", precision = 5, scale = 2)
    private BigDecimal overrideScore;

    @Column(columnDefinition = "text")
    private String comments;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
