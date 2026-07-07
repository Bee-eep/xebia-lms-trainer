package com.xebia.lms.trainer.batch.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "learner_feedback")
@Getter
@Setter
@NoArgsConstructor
public class LearnerFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "feedback_id")
    private UUID feedbackId;

    @Column(name = "trainer_id", nullable = false)
    private UUID trainerId;

    @Column(name = "learner_id", nullable = false)
    private UUID learnerId;

    @Column(name = "batch_id", nullable = false)
    private UUID batchId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
