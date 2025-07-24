-- Add address column to user table if it doesn't exist
ALTER TABLE user ADD COLUMN IF NOT EXISTS address VARCHAR(500);
