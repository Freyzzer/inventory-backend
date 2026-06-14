CREATE TABLE stock_movements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    type VARCHAR(10) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(12, 2),
    reference VARCHAR(100),
    notes VARCHAR(500),
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_stock_movements_product FOREIGN KEY (product_id) REFERENCES products(id)
);
