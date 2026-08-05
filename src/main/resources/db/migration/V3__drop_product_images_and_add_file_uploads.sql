DROP TABLE IF EXISTS product_images;

CREATE TABLE file_uploads (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    file_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    module VARCHAR(50) NOT NULL,       -- 'PRODUCT', 'USER', 'PAYMENT'
    target_id BIGINT NULL,             -- product_id, user_id, etc.
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_file_uploads_module_target ON file_uploads(module, target_id);