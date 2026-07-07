-- Author : Garv

CREATE TABLE trainer_evaluation (
    evaluation_id   UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    trainer_id      UUID NOT NULL,
    learner_id      UUID NOT NULL,
    result_id       UUID NOT NULL,
    override_score  NUMERIC(5,2),
    comments        TEXT,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_evaluation_trainer ON trainer_evaluation(trainer_id);
