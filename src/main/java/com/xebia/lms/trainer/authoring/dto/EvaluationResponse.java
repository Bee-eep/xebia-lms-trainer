/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.dto;

import com.xebia.lms.trainer.authoring.model.TrainerEvaluation;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * EvaluationResponse - output payload representing an evaluation record.
 */
public record EvaluationResponse(
        UUID evaluationId,
        UUID trainerId,
        UUID learnerId,
        UUID resultId,
        BigDecimal overrideScore,
        String comments,
        Instant createdAt
) {
    public static EvaluationResponse from(TrainerEvaluation evaluation) {
        return new EvaluationResponse(
                evaluation.getEvaluationId(),
                evaluation.getTrainerId(),
                evaluation.getLearnerId(),
                evaluation.getResultId(),
                evaluation.getOverrideScore(),
                evaluation.getComments(),
                evaluation.getCreatedAt()
        );
    }
}
