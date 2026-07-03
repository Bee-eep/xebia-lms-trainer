-- Author : Garv

CREATE TABLE course (
    course_id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    trainer_id  UUID NOT NULL,
    domain_id   UUID NOT NULL,
    title       VARCHAR(160) NOT NULL,
    summary     TEXT,
    level       VARCHAR(16) NOT NULL,   -- BEGINNER | INTERMEDIATE | ADVANCED
    version     INT NOT NULL DEFAULT 1,
    status      VARCHAR(16) NOT NULL DEFAULT 'DRAFT', -- DRAFT | PUBLISHED | ARCHIVED
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_course_trainer_id ON course(trainer_id);

CREATE TABLE course_module (
    module_id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_id   UUID NOT NULL REFERENCES course(course_id),
    title       VARCHAR(160) NOT NULL,
    sort_order  INT NOT NULL
);
CREATE INDEX idx_module_course_id ON course_module(course_id);

CREATE TABLE submodule (
    submodule_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    module_id    UUID NOT NULL REFERENCES course_module(module_id),
    title        VARCHAR(160) NOT NULL,
    sort_order   INT NOT NULL,
    est_minutes  INT
);
CREATE INDEX idx_submodule_module_id ON submodule(module_id);

CREATE TABLE content (
    content_id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    submodule_id UUID NOT NULL REFERENCES submodule(submodule_id),
    type         VARCHAR(12) NOT NULL,  -- TEXT | CODE | IMAGE | PDF | VIDEO
    body         TEXT,
    s3_key       VARCHAR(255),
    language     VARCHAR(24),
    sort_order   INT NOT NULL
);
CREATE INDEX idx_content_submodule_id ON content(submodule_id);
