/*
 * Author : Garv
 */
package com.xebia.lms.trainer.authoring.service;

import com.xebia.lms.trainer.authoring.dto.EvaluationForm;
import com.xebia.lms.trainer.authoring.model.TrainerEvaluation;
import com.xebia.lms.trainer.authoring.repository.EvaluationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * EvaluationService - manages evaluations of student test results and score overrides.
 */
@Service
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;

    public EvaluationService(EvaluationRepository evaluationRepository) {
        this.evaluationRepository = evaluationRepository;
    }

    /**
     * evaluate - records or updates a trainer's evaluation/override of a learner submission.
     */
    @Transactional
    public TrainerEvaluation evaluate(UUID trainerId, EvaluationForm form) {
        TrainerEvaluation evaluation = new TrainerEvaluation();
        evaluation.setTrainerId(trainerId);
        evaluation.setLearnerId(form.learnerId());
        evaluation.setResultId(form.resultId());
        evaluation.setOverrideScore(form.overrideScore());
        evaluation.setComments(form.comments());
        
        return evaluationRepository.save(evaluation);
    }

    /**
     * getEvaluationsByTrainer - lists all evaluations recorded by a trainer.
     */
    @Transactional(readOnly = true)
    public List<TrainerEvaluation> getEvaluationsByTrainer(UUID trainerId) {
        return evaluationRepository.findByTrainerIdOrderByCreatedAtDesc(trainerId);
    }
}
