-- Create sequence for primary key
CREATE SEQUENCE IF NOT EXISTS google_credential_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Create table
CREATE TABLE IF NOT EXISTS google_credential (
    id BIGINT PRIMARY KEY DEFAULT nextval('google_credential_seq'),
    client_id VARCHAR(255),
    client_secret VARCHAR(255),
    google_token TEXT,
    google_access_token TEXT,
    google_refresh_token TEXT,
    expires_at TIMESTAMPTZ,
    version BIGINT DEFAULT 0,
    CONSTRAINT google_credential_version_check CHECK (version >= 0)
);

-- Add comment for clarity
COMMENT ON TABLE google_credential IS 'Stores Google OAuth credentials and tokens for Drive API access.';
COMMENT ON COLUMN google_credential.id IS 'Primary key generated via sequence';
COMMENT ON COLUMN google_credential.version IS 'Optimistic locking version field';
