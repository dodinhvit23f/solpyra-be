-- Create sequence for primary key
CREATE SEQUENCE IF NOT EXISTS shopee_order_seq START 1;

-- Create shopee_order table
CREATE TABLE IF NOT EXISTS shopee_order (
    id BIGINT PRIMARY KEY DEFAULT nextval('shopee_order_seq'),

    order_id VARCHAR(255),
    status SMALLINT,
    order_date TIMESTAMPTZ,
    completed_date TIMESTAMPTZ,
    commissioned_date TIMESTAMPTZ,

    total_commission NUMERIC(28,2),
    user_commission NUMERIC(28,2),
    platform_commission NUMERIC(28,2),

    commission_rate NUMERIC(10,4),
    user_commission_rate NUMERIC(10,4),
    platform_commission_rate NUMERIC(10,4),

    product_id BIGINT,
    version BIGINT DEFAULT 0
);

-- Create hash index for order_id (fast equality search)
CREATE INDEX IF NOT EXISTS idx_shopee_order_id_hash
    ON shopee_order USING hash (order_id);

CREATE INDEX IF NOT EXISTS idx_shopee_order_product_id
    ON shopee_order (product_id);