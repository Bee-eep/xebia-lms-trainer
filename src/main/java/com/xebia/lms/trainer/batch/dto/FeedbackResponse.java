package com.xebia.lms.trainer.batch.dto;

import com.xebia.lms.trainer.batch.model.LearnerFeedback;

import java.time.Instant;
import java.util.UUID;

public record FeedbackResponse(
        UUID feedbackId,
        UUID trainerId,
        UUID learnerId,
        UUID batchId,
        String notes,
        Instant createdAt
) {
    public static FeedbackResponse from(LearnerFeedback feedback) {
        return new FeedbackResponse(
                feedback.getFeedbackId(),
                feedback.getTrainerId(),
                feedback.getLearnerId(),
                feedback.getBatchId(),
                feedback.getNotes(),
                feedback.getCreatedAt()
        );
    }
}
