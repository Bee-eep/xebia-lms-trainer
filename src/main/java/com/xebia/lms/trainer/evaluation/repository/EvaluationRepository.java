/*
 * Author : Garv
 */
package com.xebia.lms.trainer.evaluation.repository;

import com.xebia.lms.trainer.evaluation.model.TrainerEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * EvaluationRepository - JPA operations on trainer_evaluation.
 */
public interface EvaluationRepository extends JpaRepository<TrainerEvaluation, UUID> {
    
    /**
     * findByTrainerIdOrderByCreatedAtDesc - returns evaluations in reverse chronological order.
     */
    List<TrainerEvaluation> findByTrainerIdOrderByCreatedAtDesc(UUID trainerId);
}
