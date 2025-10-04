CREATE SEQUENCE IF NOT EXISTS users_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY DEFAULT nextval('users_id_seq'),
    version BIGINT DEFAULT 0,
    user_name VARCHAR(30) NOT NULL,
    password VARCHAR(500),
    email VARCHAR(255),
    phone_number VARCHAR(30),
    gender VARCHAR(6),
    otp_secret VARCHAR(255),
    have_mfa BOOLEAN DEFAULT false,
    isssouser BOOLEAN DEFAULT false,
    create_date TIMESTAMP,
    update_date DATE,
    role_id BIGINT NOT NULL,
    isdeleted BOOLEAN DEFAULT false,
    CONSTRAINT uk_user_name UNIQUE (user_name),
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES role (id)
    );

-- ==========================
-- Indexes
-- ==========================
CREATE INDEX IF NOT EXISTS idx_username ON users(user_name);