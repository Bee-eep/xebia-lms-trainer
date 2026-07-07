package com.xebia.lms.trainer.batch.repository;

import com.xebia.lms.trainer.batch.model.LearnerFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LearnerFeedbackRepository extends JpaRepository<LearnerFeedback, UUID> {
    List<LearnerFeedback> findByBatchIdOrderByCreatedAtDesc(UUID batchId);
}
