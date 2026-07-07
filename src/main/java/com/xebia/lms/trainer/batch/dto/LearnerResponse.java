/*
 * Author : Himanshu Verma
 */
package com.xebia.lms.trainer.batch.dto;

import com.xebia.lms.trainer.batch.model.Learner;

import java.time.Instant;
import java.util.UUID;

public record LearnerResponse(
        UUID learnerId,
        UUID batchId,
        String name,
        String email,
        Instant createdAt
) {
    public static LearnerResponse from(Learner learner) {
        return new LearnerResponse(
                learner.getLearnerId(),
                learner.getBatchId(),
                learner.getName(),
                learner.getEmail(),
                learner.getCreatedAt()
        );
    }
}
