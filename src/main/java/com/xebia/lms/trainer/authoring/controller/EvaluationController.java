/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.controller;

import com.xebia.lms.trainer.authoring.dto.EvaluationForm;
import com.xebia.lms.trainer.authoring.dto.EvaluationResponse;
import com.xebia.lms.trainer.authoring.model.TrainerEvaluation;
import com.xebia.lms.trainer.authoring.service.EvaluationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * EvaluationController - REST controller for Trainer evaluations and overrides.
 */
@RestController
@RequestMapping("/api/v1/trainer/evaluations")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    /**
     * evaluate - POST /api/v1/trainer/evaluations
     * Records or overrides a learner test submission evaluation.
     */
    @PostMapping
    public ResponseEntity<EvaluationResponse> evaluate(
            @RequestHeader("X-Trainer-Id") UUID trainerId,
            @Valid @RequestBody EvaluationForm form) {
        TrainerEvaluation evaluation = evaluationService.evaluate(trainerId, form);
        return ResponseEntity.status(HttpStatus.CREATED).body(EvaluationResponse.from(evaluation));
    }

    /**
     * getAllEvaluations - GET /api/v1/trainer/evaluations
     * Returns all evaluations recorded by the calling trainer.
     */
    @GetMapping
    public ResponseEntity<List<EvaluationResponse>> getAllEvaluations(
            @RequestHeader("X-Trainer-Id") UUID trainerId) {
        List<TrainerEvaluation> evaluations = evaluationService.getEvaluationsByTrainer(trainerId);
        List<EvaluationResponse> responses = evaluations.stream()
                .map(EvaluationResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
