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

    @PostMapping
    public ResponseEntity<BatchResponse> createBatch(
            @RequestHeader("X-Trainer-Id") UUID trainerId,
            @RequestBody com.xebia.lms.trainer.batch.dto.BatchRequest request) {
        Batch batch = batchService.createBatch(trainerId, request);
        return ResponseEntity.ok(BatchResponse.from(batch));
    }

    @PostMapping("/{id}/learners")
    public ResponseEntity<LearnerResponse> addLearner(
            @RequestHeader("X-Trainer-Id") UUID trainerId,
            @PathVariable("id") UUID batchId,
            @RequestBody com.xebia.lms.trainer.batch.dto.LearnerRequest request) {
        Learner learner = batchService.addLearner(trainerId, batchId, request);
        return ResponseEntity.ok(LearnerResponse.from(learner));
    }

    @DeleteMapping("/{batchId}/learners/{learnerId}")
    public ResponseEntity<Void> removeLearner(
            @RequestHeader("X-Trainer-Id") UUID trainerId,
            @PathVariable("batchId") UUID batchId,
            @PathVariable("learnerId") UUID learnerId) {
        batchService.removeLearner(trainerId, batchId, learnerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{batchId}/feedback")
    public ResponseEntity<List<com.xebia.lms.trainer.batch.dto.FeedbackResponse>> getFeedback(
            @RequestHeader("X-Trainer-Id") UUID trainerId,
            @PathVariable("batchId") UUID batchId) {
        List<com.xebia.lms.trainer.batch.model.LearnerFeedback> feedbackList = batchService.getFeedbackByBatch(trainerId, batchId);
        List<com.xebia.lms.trainer.batch.dto.FeedbackResponse> responses = feedbackList.stream()
                .map(com.xebia.lms.trainer.batch.dto.FeedbackResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{batchId}/learners/{learnerId}/feedback")
    public ResponseEntity<com.xebia.lms.trainer.batch.dto.FeedbackResponse> createFeedback(
            @RequestHeader("X-Trainer-Id") UUID trainerId,
            @PathVariable("batchId") UUID batchId,
            @PathVariable("learnerId") UUID learnerId,
            @RequestBody com.xebia.lms.trainer.batch.dto.FeedbackRequest request) {
        com.xebia.lms.trainer.batch.model.LearnerFeedback feedback = batchService.createFeedback(trainerId, batchId, learnerId, request);
        return ResponseEntity.ok(com.xebia.lms.trainer.batch.dto.FeedbackResponse.from(feedback));
    }
}
