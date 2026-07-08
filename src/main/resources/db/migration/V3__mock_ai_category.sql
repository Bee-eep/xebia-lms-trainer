-- Create Mock Category for AI
INSERT INTO category (category_id, name, icon, color, description, active)
VALUES (
    'b5c1c8a1-8d26-4071-88c9-76b9116e9b46',
    'Artificial Intelligence',
    '🤖',
    '#6c5ce7',
    'Learn about AI, Machine Learning, and Neural Networks.',
    true
) ON CONFLICT (name) DO NOTHING;

-- Create Mock Course for AI
INSERT INTO course (course_id, trainer_id, category_id, title, summary, level, version, status)
VALUES (
    'c0f7b093-559d-4e92-9426-17b2b07bc4bc',
    '99999999-9999-9999-9999-999999999999',
    'b5c1c8a1-8d26-4071-88c9-76b9116e9b46',
    'Introduction to Deep Learning',
    'A comprehensive guide to understanding deep neural networks and their applications.',
    'BEGINNER',
    1,
    'PUBLISHED'
) ON CONFLICT DO NOTHING;

-- Create Mock Modules
INSERT INTO course_module (module_id, course_id, title, sort_order)
VALUES 
    ('d7b41b9c-70fc-4c8d-96cf-c9c0fb5cc0a2', 'c0f7b093-559d-4e92-9426-17b2b07bc4bc', 'Module 1: Foundations of AI', 1),
    ('e0e0a5c4-54c3-448c-9c98-1e47f7d3d2a7', 'c0f7b093-559d-4e92-9426-17b2b07bc4bc', 'Module 2: Neural Networks', 2),
    ('f1d9a2b5-5c1a-4f5c-8d1a-4c9f0b1a2c3d', 'c0f7b093-559d-4e92-9426-17b2b07bc4bc', 'Module 3: Advanced Architectures', 3)
ON CONFLICT DO NOTHING;

-- Create Mock Submodules
INSERT INTO submodule (submodule_id, module_id, title, sort_order, est_minutes)
VALUES
    ('1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d', 'd7b41b9c-70fc-4c8d-96cf-c9c0fb5cc0a2', 'What is AI?', 1, 15),
    ('2b3c4d5e-6f7a-8b9c-0d1e-2f3a4b5c6d7e', 'e0e0a5c4-54c3-448c-9c98-1e47f7d3d2a7', 'Perceptrons', 1, 30),
    ('3c4d5e6f-7a8b-9c0d-1e2f-3a4b5c6d7e8f', 'f1d9a2b5-5c1a-4f5c-8d1a-4c9f0b1a2c3d', 'Transformers and LLMs', 1, 45)
ON CONFLICT DO NOTHING;

-- Create Mock Content
INSERT INTO content (content_id, submodule_id, type, heading_level, body, sort_order)
VALUES
    ('4d5e6f7a-8b9c-0d1e-2f3a-4b5c6d7e8f9a', '1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d', 'TEXT', 'H1', 'Welcome to AI', 1),
    ('5e6f7a8b-9c0d-1e2f-3a4b-5c6d7e8f9a0b', '1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d', 'TEXT', 'H3', 'In this submodule we explore what AI means today.', 2),
    ('6f7a8b9c-0d1e-2f3a-4b5c-6d7e8f9a0b1c', '2b3c4d5e-6f7a-8b9c-0d1e-2f3a4b5c6d7e', 'TEXT', 'H2', 'The Building Blocks', 1),
    ('7a8b9c0d-1e2f-3a4b-5c6d-7e8f9a0b1c2d', '3c4d5e6f-7a8b-9c0d-1e2f-3a4b5c6d7e8f', 'TEXT', 'H2', 'Attention is All You Need', 1)
ON CONFLICT DO NOTHING;
