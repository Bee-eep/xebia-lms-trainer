/*
 * Author : Himanshu Verma
 */
package com.xebia.lms.trainer.batch.controller;

import com.xebia.lms.trainer.batch.dto.BatchResponse;
import com.xebia.lms.trainer.batch.dto.LearnerResponse;
import com.xebia.lms.trainer.batch.model.Batch;
import com.xebia.lms.trainer.batch.model.Learner;
import com.xebia.lms.trainer.batch.service.BatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trainer/batches")
public class BatchController {

    private final BatchService batchService;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    /**
     * getBatches - GET /api/v1/trainer/batches
     * Returns a list of all batches assigned to the calling trainer.
     */
    @GetMapping
    public ResponseEntity<List<BatchResponse>> getBatches(@RequestHeader("X-Trainer-Id") UUID trainerId) {
        List<Batch> batches = batchService.getBatchesByTrainer(trainerId);
        List<BatchResponse> responses = batches.stream()
                .map(BatchResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    /**
     * getLearnersByBatch - GET /api/v1/trainer/batches/{id}/learners
     * Returns a list of learners for a specific batch, provided the batch belongs to the calling trainer.
     */
    @GetMapping("/{id}/learners")
    public ResponseEntity<List<LearnerResponse>> getLearnersByBatch(
            @RequestHeader("X-Trainer-Id") UUID trainerId,
            @PathVariable("id") UUID batchId) {
        List<Learner> learners = batchService.getLearnersByBatch(trainerId, batchId);
        List<LearnerResponse> responses = learners.stream()
                .map(LearnerResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
