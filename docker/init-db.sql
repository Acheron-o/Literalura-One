-- BookVerse Database Initialization Script
-- This script initializes the PostgreSQL database with proper configuration

-- Set database encoding and collation
SET client_encoding = 'UTF8';
SET timezone = 'UTC';

-- Create extensions if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_stat_statements";

-- Set default privileges
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO bookverse_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO bookverse_user;

-- Create search configuration for full-text search
CREATE TEXT SEARCH CONFIGURATION bookverse_search (COPY = english);

-- Log initialization completion
DO $$
BEGIN
    RAISE NOTICE 'Literalura database initialized successfully';
END $$;
