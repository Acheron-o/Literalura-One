-- LiterAlura Database Initialization Script
-- This script initializes the PostgreSQL database with proper configuration

-- Set database encoding and collation
SET client_encoding = 'UTF8';
SET timezone = 'UTC';

-- Create extensions if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_stat_statements";

-- Set default privileges
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO literalura_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO literalura_user;

-- Create search configuration for full-text search
CREATE TEXT SEARCH CONFIGURATION literalura_search (COPY = english);

-- Log initialization completion
DO $$
BEGIN
<<<<<<< HEAD
    RAISE NOTICE 'LiterAlura database initialized successfully';
=======
    RAISE NOTICE 'Literalura database initialized successfully';
>>>>>>> adf1ed401129b9668d77ac868af8f137f7126d7b
END $$;
