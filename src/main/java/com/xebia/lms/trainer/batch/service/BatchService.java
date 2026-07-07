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

import java.util.List;
import java.util.UUID;

@Service
public class BatchService {

    private final BatchRepository batchRepository;
    private final LearnerRepository learnerRepository;

    public BatchService(BatchRepository batchRepository, LearnerRepository learnerRepository) {
        this.batchRepository = batchRepository;
        this.learnerRepository = learnerRepository;
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
}
