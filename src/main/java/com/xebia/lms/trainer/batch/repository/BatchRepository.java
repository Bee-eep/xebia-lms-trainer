/*
 * Author : Himanshu Verma
 */
package com.xebia.lms.trainer.batch.repository;

import com.xebia.lms.trainer.batch.model.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BatchRepository extends JpaRepository<Batch, UUID> {
    List<Batch> findByTrainerIdOrderByCreatedAtDesc(UUID trainerId);
}
