CREATE SEQUENCE IF NOT EXISTS user_shopee_order_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS user_shopee_order (
    id BIGINT PRIMARY KEY DEFAULT nextval('user_shopee_order_seq'),
    version BIGINT DEFAULT 0,
    user_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    create_date TIMESTAMPTZ,
    payment_approved boolean
);