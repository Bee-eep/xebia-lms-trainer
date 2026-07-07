package com.xebia.lms.trainer.batch.config;

import com.xebia.lms.trainer.batch.model.Batch;
import com.xebia.lms.trainer.batch.model.Learner;
import com.xebia.lms.trainer.batch.repository.BatchRepository;
import com.xebia.lms.trainer.batch.repository.LearnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Seeds mock batch and learner data on startup if none exists.
 * Uses a well-known trainer UUID so the frontend can discover batches
 * regardless of the localStorage-generated trainer ID — the UI lets
 * users paste any trainer ID into the header field.
 *
 * Seeded Trainer ID: 00000000-0000-4000-a000-000000000001
 */
@Component
public class BatchDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(BatchDataSeeder.class);

    // Well-known trainer UUID for mock data
    private static final UUID TRAINER_ID = UUID.fromString("00000000-0000-4000-a000-000000000001");

    private final BatchRepository batchRepository;
    private final LearnerRepository learnerRepository;
    private final com.xebia.lms.trainer.batch.repository.LearnerFeedbackRepository learnerFeedbackRepository;

    public BatchDataSeeder(BatchRepository batchRepository, LearnerRepository learnerRepository,
                           com.xebia.lms.trainer.batch.repository.LearnerFeedbackRepository learnerFeedbackRepository) {
        this.batchRepository = batchRepository;
        this.learnerRepository = learnerRepository;
        this.learnerFeedbackRepository = learnerFeedbackRepository;
    }

    @Override
    public void run(String... args) {
        List<Batch> existing = batchRepository.findByTrainerIdOrderByCreatedAtDesc(TRAINER_ID);
        if (!existing.isEmpty()) {
            log.info("BatchDataSeeder: {} batches already exist for trainer {}, skipping seed.", existing.size(), TRAINER_ID);
            return;
        }

        log.info("BatchDataSeeder: Seeding mock batches and learners for trainer {}...", TRAINER_ID);
        Instant now = Instant.now();

        // ── Batch 1: Spring Boot Masterclass ────────────────────────
        Batch batch1 = new Batch();
        batch1.setTrainerId(TRAINER_ID);
        batch1.setName("Spring Boot Masterclass — Cohort 7");
        batch1.setStartDate(now.minus(30, ChronoUnit.DAYS));
        batch1.setEndDate(now.plus(60, ChronoUnit.DAYS));
        batch1.setCreatedAt(now.minus(35, ChronoUnit.DAYS));
        batch1 = batchRepository.save(batch1);

        List<Learner> learners1 = seedLearners(batch1.getBatchId(), List.of(
                new String[]{"Alice Johnson", "alice.johnson@xebia.com"},
                new String[]{"Bob Smith", "bob.smith@xebia.com"},
                new String[]{"Charlie Brown", "charlie.brown@xebia.com"},
                new String[]{"Diana Martinez", "diana.martinez@xebia.com"},
                new String[]{"Ethan Williams", "ethan.williams@xebia.com"}
        ), now.minus(30, ChronoUnit.DAYS));

        // Seed some mock feedback
        if (!learners1.isEmpty()) {
            com.xebia.lms.trainer.batch.model.LearnerFeedback fb1 = new com.xebia.lms.trainer.batch.model.LearnerFeedback();
            fb1.setTrainerId(TRAINER_ID);
            fb1.setBatchId(batch1.getBatchId());
            fb1.setLearnerId(learners1.get(0).getLearnerId()); // Alice
            fb1.setNotes("Excellent participation in labs. Demonstrates a strong understanding of Spring IOC and dependency injection principles.");
            fb1.setCreatedAt(now.minus(20, ChronoUnit.DAYS));
            learnerFeedbackRepository.save(fb1);

            com.xebia.lms.trainer.batch.model.LearnerFeedback fb2 = new com.xebia.lms.trainer.batch.model.LearnerFeedback();
            fb2.setTrainerId(TRAINER_ID);
            fb2.setBatchId(batch1.getBatchId());
            fb2.setLearnerId(learners1.get(1).getLearnerId()); // Bob
            fb2.setNotes("Completed the database migrations module. Needs assistance with complex JPA entity mappings but overall performing well.");
            fb2.setCreatedAt(now.minus(15, ChronoUnit.DAYS));
            learnerFeedbackRepository.save(fb2);
        }

        // ── Batch 2: Cloud-Native Architecture ──────────────────────
        Batch batch2 = new Batch();
        batch2.setTrainerId(TRAINER_ID);
        batch2.setName("Cloud-Native Architecture — Q3 2026");
        batch2.setStartDate(now.minus(10, ChronoUnit.DAYS));
        batch2.setEndDate(now.plus(80, ChronoUnit.DAYS));
        batch2.setCreatedAt(now.minus(15, ChronoUnit.DAYS));
        batch2 = batchRepository.save(batch2);

        seedLearners(batch2.getBatchId(), List.of(
                new String[]{"Fatima Khan", "fatima.khan@xebia.com"},
                new String[]{"George Lee", "george.lee@xebia.com"},
                new String[]{"Hannah Patel", "hannah.patel@xebia.com"}
        ), now.minus(10, ChronoUnit.DAYS));

        // ── Batch 3: React & TypeScript Fundamentals ────────────────
        Batch batch3 = new Batch();
        batch3.setTrainerId(TRAINER_ID);
        batch3.setName("React & TypeScript Fundamentals — Summer 2026");
        batch3.setStartDate(now.plus(5, ChronoUnit.DAYS));
        batch3.setEndDate(now.plus(90, ChronoUnit.DAYS));
        batch3.setCreatedAt(now.minus(3, ChronoUnit.DAYS));
        batch3 = batchRepository.save(batch3);

        seedLearners(batch3.getBatchId(), List.of(
                new String[]{"Ivan Novak", "ivan.novak@xebia.com"},
                new String[]{"Julia Chen", "julia.chen@xebia.com"},
                new String[]{"Kevin O'Brien", "kevin.obrien@xebia.com"},
                new String[]{"Lara Singh", "lara.singh@xebia.com"}
        ), now.minus(2, ChronoUnit.DAYS));

        log.info("BatchDataSeeder: Seeded 3 batches with 12 learners total.");
    }

    private List<Learner> seedLearners(UUID batchId, List<String[]> learnerData, Instant baseDate) {
        java.util.List<Learner> list = new java.util.ArrayList<>();
        for (int i = 0; i < learnerData.size(); i++) {
            Learner learner = new Learner();
            learner.setBatchId(batchId);
            learner.setName(learnerData.get(i)[0]);
            learner.setEmail(learnerData.get(i)[1]);
            learner.setCreatedAt(baseDate.plus(i, ChronoUnit.HOURS));
            list.add(learnerRepository.save(learner));
        }
        return list;
    }
}
