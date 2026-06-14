ALTER TABLE stock_movements
    ADD COLUMN user_id BIGINT NOT NULL,
    ADD CONSTRAINT fk_stock_movements_user FOREIGN KEY (user_id) REFERENCES users(id);
