package com.xebia.lms.trainer.batch.dto;

import java.time.Instant;

public record BatchRequest(
        String name,
        Instant startDate,
        Instant endDate
) {}
