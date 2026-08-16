-- Add sequence_number column to interview_responses table
ALTER TABLE interview_responses 
ADD COLUMN sequence_number INTEGER NOT NULL DEFAULT 1;
