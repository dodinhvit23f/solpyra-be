CREATE SEQUENCE IF NOT EXISTS commission_outbox_seq START 1 INCREMENT 1;

CREATE TABLE IF NOT EXISTS commission_outbox (
    id BIGINT PRIMARY KEY DEFAULT nextval('commission_outbox_seq'),
    order_id BIGINT NOT NULL,
    commission NUMERIC(28,2),
    user_id BIGINT NOT NULL,
    status SMALLINT NOT NULL,
    retry INT DEFAULT 0,
    error_message VARCHAR(1000),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    sent_at TIMESTAMPTZ,
    version BIGINT DEFAULT 0
    );

CREATE INDEX IF NOT EXISTS idx_commission_outbox_status ON commission_outbox(status);