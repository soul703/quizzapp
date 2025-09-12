-- Flyway Migration: V1
-- Description: Creates the core tables for persisting aggregates (Write-Side).
-- This includes users, questions, quizzes, and live_sessions with production-ready features.

-- =================================================================
-- Table: users
-- Stores user account information for authentication and authorization.
-- =================================================================
CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       first_name VARCHAR(100),
                       last_name VARCHAR(100),
                       role VARCHAR(20) NOT NULL CHECK (role IN ('TEACHER', 'STUDENT')),
                       status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'LOCKED')),
                       password_failed_count INT NOT NULL DEFAULT 0,

    -- Auditing Columns
                       created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                       updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                       deleted_at TIMESTAMPTZ -- For soft deletes (NULL means not deleted)
);


-- =================================================================
-- Table: questions
-- Stores individual, reusable questions for the Question Bank.
-- =================================================================
CREATE TABLE questions (
                           id UUID PRIMARY KEY,
                           owner_id UUID NOT NULL,
                           text TEXT NOT NULL,
                           type VARCHAR(20) NOT NULL CHECK (type IN ('MCQ', 'TRUE_FALSE', 'SHORT_ANSWER')),
                           options JSONB NOT NULL DEFAULT '[]'::jsonb,
                           correct_answer JSONB NOT NULL,
                           metadata JSONB NOT NULL DEFAULT '{}'::jsonb, -- For tags, difficulty, subject, etc.

    -- Auditing Columns
                           created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                           updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                           deleted_at TIMESTAMPTZ,

                           CONSTRAINT fk_questions_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE RESTRICT
);

-- Indexes for questions table
CREATE INDEX idx_questions_owner_id ON questions(owner_id);
CREATE INDEX idx_questions_metadata_gin ON questions USING GIN (metadata); -- GIN index for efficient JSONB searching


-- =================================================================
-- Table: quizzes
-- Stores the structure and metadata of a quiz.
-- =================================================================
CREATE TABLE quizzes (
                         id UUID PRIMARY KEY,
                         owner_id UUID NOT NULL,
                         title VARCHAR(255) NOT NULL,
                         description TEXT,
                         status VARCHAR(20) NOT NULL CHECK (status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED')),
                         question_entries JSONB NOT NULL DEFAULT '[]'::jsonb,

    -- Auditing Columns
                         created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                         updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                         deleted_at TIMESTAMPTZ,

                         CONSTRAINT fk_quizzes_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE RESTRICT
);

-- Index for quizzes table
CREATE INDEX idx_quizzes_owner_id ON quizzes(owner_id);


-- =================================================================
-- Table: live_sessions
-- Stores the complete state of a Live Game Mode session.
-- =================================================================
CREATE TABLE live_sessions (
                               id UUID PRIMARY KEY,
                               host_id UUID NOT NULL,
                               room_code VARCHAR(10) NOT NULL,
                               status VARCHAR(30) NOT NULL CHECK (status IN ('LOBBY', 'QUESTION_IN_PROGRESS', 'LEADERBOARD_SHOWN', 'FINISHED')),
                               quiz_snapshot JSONB NOT NULL,
                               participants JSONB NOT NULL DEFAULT '{}'::jsonb,
                               current_question_index INT NOT NULL DEFAULT -1,
                               current_question_start_time TIMESTAMPTZ,

    -- Auditing Columns
                               created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                               updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                               CONSTRAINT fk_live_sessions_host FOREIGN KEY (host_id) REFERENCES users(id) ON DELETE RESTRICT
);

-- Partial unique index: Ensures room_code is unique ONLY for non-finished sessions.
CREATE UNIQUE INDEX idx_live_sessions_active_room_code
    ON live_sessions(room_code)
    WHERE status <> 'FINISHED';