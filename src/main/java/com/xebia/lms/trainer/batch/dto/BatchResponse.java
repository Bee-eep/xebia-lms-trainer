/*
 * Author : Himanshu Verma
 */
package com.xebia.lms.trainer.batch.dto;

import com.xebia.lms.trainer.batch.model.Batch;

import java.time.Instant;
import java.util.UUID;

public record BatchResponse(
        UUID batchId,
        UUID trainerId,
        String name,
        Instant startDate,
        Instant endDate,
        Instant createdAt
) {
    public static BatchResponse from(Batch batch) {
        return new BatchResponse(
                batch.getBatchId(),
                batch.getTrainerId(),
                batch.getName(),
                batch.getStartDate(),
                batch.getEndDate(),
                batch.getCreatedAt()
        );
    }
}
