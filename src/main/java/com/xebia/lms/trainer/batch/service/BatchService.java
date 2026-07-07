/*
 * Author : Himanshu Verma
 */
package com.xebia.lms.trainer.batch.service;

import com.xebia.lms.trainer.batch.model.Batch;
import com.xebia.lms.trainer.batch.model.Learner;
import com.xebia.lms.trainer.batch.repository.BatchRepository;
import com.xebia.lms.trainer.batch.repository.LearnerRepository;
import com.xebia.lms.trainer.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class BatchService {

    private final BatchRepository batchRepository;
    private final LearnerRepository learnerRepository;
    private final com.xebia.lms.trainer.batch.repository.LearnerFeedbackRepository learnerFeedbackRepository;

    public BatchService(BatchRepository batchRepository, LearnerRepository learnerRepository, 
                        com.xebia.lms.trainer.batch.repository.LearnerFeedbackRepository learnerFeedbackRepository) {
        this.batchRepository = batchRepository;
        this.learnerRepository = learnerRepository;
        this.learnerFeedbackRepository = learnerFeedbackRepository;
    }

    @Transactional(readOnly = true)
    public List<Batch> getBatchesByTrainer(UUID trainerId) {
        return batchRepository.findByTrainerIdOrderByCreatedAtDesc(trainerId);
    }

    @Transactional(readOnly = true)
    public List<Learner> getLearnersByBatch(UUID trainerId, UUID batchId) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found: " + batchId));

        if (!batch.getTrainerId().equals(trainerId)) {
            throw new IllegalArgumentException("Trainer " + trainerId + " is not assigned to batch " + batchId);
        }

        return learnerRepository.findByBatchIdOrderByCreatedAtDesc(batchId);
    }

    @Transactional
    public Batch createBatch(UUID trainerId, com.xebia.lms.trainer.batch.dto.BatchRequest request) {
        Batch batch = new Batch();
        batch.setTrainerId(trainerId);
        batch.setName(request.name());
        batch.setStartDate(request.startDate());
        batch.setEndDate(request.endDate());
        batch.setCreatedAt(Instant.now());
        return batchRepository.save(batch);
    }

    @Transactional
    public Learner addLearner(UUID trainerId, UUID batchId, com.xebia.lms.trainer.batch.dto.LearnerRequest request) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found: " + batchId));

        if (!batch.getTrainerId().equals(trainerId)) {
            throw new IllegalArgumentException("Trainer " + trainerId + " is not assigned to batch " + batchId);
        }

        Learner learner = new Learner();
        learner.setBatchId(batchId);
        learner.setName(request.name());
        learner.setEmail(request.email());
        learner.setCreatedAt(Instant.now());
        return learnerRepository.save(learner);
    }

    @Transactional
    public void removeLearner(UUID trainerId, UUID batchId, UUID learnerId) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found: " + batchId));

        if (!batch.getTrainerId().equals(trainerId)) {
            throw new IllegalArgumentException("Trainer " + trainerId + " is not assigned to batch " + batchId);
        }

        Learner learner = learnerRepository.findById(learnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Learner not found: " + learnerId));

        if (!learner.getBatchId().equals(batchId)) {
            throw new IllegalArgumentException("Learner " + learnerId + " is not enrolled in batch " + batchId);
        }

        learnerRepository.delete(learner);
    }

    @Transactional
    public com.xebia.lms.trainer.batch.model.LearnerFeedback createFeedback(
            UUID trainerId, UUID batchId, UUID learnerId, com.xebia.lms.trainer.batch.dto.FeedbackRequest request) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found: " + batchId));

        if (!batch.getTrainerId().equals(trainerId)) {
            throw new IllegalArgumentException("Trainer " + trainerId + " is not assigned to batch " + batchId);
        }

        Learner learner = learnerRepository.findById(learnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Learner not found: " + learnerId));

        if (!learner.getBatchId().equals(batchId)) {
            throw new IllegalArgumentException("Learner " + learnerId + " is not enrolled in batch " + batchId);
        }

        com.xebia.lms.trainer.batch.model.LearnerFeedback feedback = new com.xebia.lms.trainer.batch.model.LearnerFeedback();
        feedback.setTrainerId(trainerId);
        feedback.setBatchId(batchId);
        feedback.setLearnerId(learnerId);
        feedback.setNotes(request.notes());
        feedback.setCreatedAt(Instant.now());
        
        return learnerFeedbackRepository.save(feedback);
    }

    @Transactional(readOnly = true)
    public List<com.xebia.lms.trainer.batch.model.LearnerFeedback> getFeedbackByBatch(UUID trainerId, UUID batchId) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found: " + batchId));

        if (!batch.getTrainerId().equals(trainerId)) {
            throw new IllegalArgumentException("Trainer " + trainerId + " is not assigned to batch " + batchId);
        }

        return learnerFeedbackRepository.findByBatchIdOrderByCreatedAtDesc(batchId);
    }
}
