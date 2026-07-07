/*
 * Author : Himanshu Verma
 */
package com.xebia.lms.trainer.batch.repository;

import com.xebia.lms.trainer.batch.model.Learner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LearnerRepository extends JpaRepository<Learner, UUID> {
    List<Learner> findByBatchIdOrderByCreatedAtDesc(UUID batchId);
}
