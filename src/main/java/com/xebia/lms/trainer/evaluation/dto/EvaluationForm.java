/*
 * Author : Garv
 */
package com.xebia.lms.trainer.evaluation.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * EvaluationForm - request payload to submit/override evaluation of a learner result.
 */
public record EvaluationForm(
        @NotNull(message = "learnerId is required")
        UUID learnerId,

        @NotNull(message = "resultId is required")
        UUID resultId,

        BigDecimal overrideScore,

        String comments
) {
}
