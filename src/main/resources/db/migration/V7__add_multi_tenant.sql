CREATE TABLE companies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    plan VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'OWNER'
);

ALTER TABLE products
    ADD COLUMN company_id BIGINT NOT NULL,
    ADD CONSTRAINT fk_products_company FOREIGN KEY (company_id) REFERENCES companies(id);

ALTER TABLE products DROP INDEX sku;

ALTER TABLE products
    ADD CONSTRAINT uq_products_sku_company UNIQUE (sku, company_id);

ALTER TABLE users
    ADD COLUMN company_id BIGINT NOT NULL,
    ADD CONSTRAINT fk_users_company FOREIGN KEY (company_id) REFERENCES companies(id);

ALTER TABLE categories
    ADD COLUMN company_id BIGINT NOT NULL,
    ADD CONSTRAINT fk_categories_company FOREIGN KEY (company_id) REFERENCES companies(id);

ALTER TABLE categories DROP INDEX name;
