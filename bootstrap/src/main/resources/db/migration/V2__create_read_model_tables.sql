-- Flyway Migration: V2
-- Description: Creates the denormalized read-side tables (projections)
-- optimized for fast UI queries.

-- =================================================================
-- Table: quiz_summaries
-- Powers the teacher's dashboard for listing their quizzes.
-- =================================================================
CREATE TABLE quiz_summaries (
                                quiz_id UUID PRIMARY KEY,
                                owner_id UUID NOT NULL,
                                title VARCHAR(255) NOT NULL,
                                description_snippet VARCHAR(255),
                                question_count INT NOT NULL DEFAULT 0,
                                status VARCHAR(20) NOT NULL,
                                tags TEXT[], -- Array of tags for easy filtering
                                updated_at TIMESTAMPTZ NOT NULL
);

-- Index for fast lookup of a teacher's quizzes
CREATE INDEX idx_quiz_summaries_owner_id ON quiz_summaries(owner_id);


-- =================================================================
-- Table: quiz_details
-- Powers the detailed view/edit page for a single quiz.
-- Contains fully denormalized data to render the page with one query.
-- =================================================================
CREATE TABLE quiz_details (
                              quiz_id UUID PRIMARY KEY,
                              owner_id UUID NOT NULL,
                              title VARCHAR(255) NOT NULL,
                              description TEXT,
                              status VARCHAR(20) NOT NULL,
    -- A complete, denormalized copy of all question data for this quiz
                              questions_data JSONB NOT NULL DEFAULT '[]'::jsonb,
                              updated_at TIMESTAMPTZ NOT NULL
);


-- =================================================================
-- Table: session_results
-- Caches the final outcome of a session for post-game reports.
-- =================================================================
CREATE TABLE session_results (
                                 session_id UUID PRIMARY KEY,
                                 quiz_id UUID NOT NULL,
                                 host_id UUID NOT NULL,
                                 quiz_title VARCHAR(255) NOT NULL,
                                 participant_count INT NOT NULL,
                                 finished_at TIMESTAMPTZ NOT NULL,
    -- Stores the final ranked leaderboard as a JSON array
                                 leaderboard JSONB NOT NULL
);

-- Index for fast lookup of a host's past sessions
CREATE INDEX idx_session_results_host_id ON session_results(host_id);