CREATE SEQUENCE IF NOT EXISTS shopee_product_seq START 1;

CREATE TABLE IF NOT EXISTS shopee_product (
    id BIGINT PRIMARY KEY DEFAULT nextval('shopee_product_seq'),
    version BIGINT DEFAULT 0,
    product_code VARCHAR(30),
    product_name VARCHAR(1000),
    store_id VARCHAR(30),
    store_name VARCHAR(1000),
    affiliate_link VARCHAR(1000)
);

-- Enable pg_trgm extension (for substring search)
--CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Create hash indexes for exact lookups
CREATE INDEX IF NOT EXISTS idx_shopee_product_code_hash
    ON shopee_product USING hash (product_code);

CREATE INDEX IF NOT EXISTS idx_shopee_store_id_hash
    ON shopee_product USING hash (store_id);

-- Create GIN indexes for substring searches
CREATE INDEX IF NOT EXISTS idx_shopee_product_name_trgm
    ON shopee_product USING gin (product_name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_shopee_store_name_trgm
    ON shopee_product USING gin (store_name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_shopee_product_id
    ON shopee_product (id);