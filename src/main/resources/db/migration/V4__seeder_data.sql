-- 1. SEED USERS (Password for all: 'Password123!')
INSERT INTO users (name, email, password, phone, role, status) VALUES
('System Admin', 'admin@example.com', '$2a$10$7R.q1.3fHjC4m2B9Z3Q2.uI5X1K6N.o0dM8K8k2e3f4g5h6i7j8k', '012345678', 'ADMIN', 'ACTIVE'),
('John Doe', 'john.doe@example.com', '$2a$10$7R.q1.3fHjC4m2B9Z3Q2.uI5X1K6N.o0dM8K8k2e3f4g5h6i7j8k', '098765432', 'CUSTOMER', 'ACTIVE'),
('Sreyroth Mao', 'sreyroth@example.com', '$2a$10$7R.q1.3fHjC4m2B9Z3Q2.uI5X1K6N.o0dM8K8k2e3f4g5h6i7j8k', '0881234567', 'CUSTOMER', 'ACTIVE');

-- 2. SEED ADDRESSES (Assumes user IDs generated above)
INSERT INTO addresses (user_id, full_name, phone, address_line, city, province, postal_code, is_default, latitude, longitude) VALUES
(1, 'System Admin', '012345678', 'Monivong Blvd', 'Phnom Penh', 'Phnom Penh', '12301', TRUE, 11.550000, 104.920000),
(2, 'John Doe', '098765432', 'Street 271, Sangkat Takhmao', 'Phnom Penh', 'Kandal', '12000', TRUE, 11.483333, 104.950000);

-- 3. SEED SUB-CATEGORIES (Parent IDs refer to root categories inserted in V2)
INSERT INTO categories (name, status) VALUES
('Smartphones', 'ACTIVE'),
('Laptops',  'ACTIVE'),
('Accessories', 'ACTIVE');

-- 4. SEED PRODUCTS
INSERT INTO products (category_id, name, description, price, stock_quantity, image_url, status) VALUES
(3, 'iPhone 16 Pro', 'Apple A18 Pro Chip, 256GB Titanium', 100.00, 50, 'http://localhost:8080/uploads/iphone16.jpg', 'ACTIVE'),
(3, 'Samsung Galaxy S24', 'Snapdragon 8 Gen 3, 256GB', 100.00, 30, 'http://localhost:8080/uploads/s24.jpg', 'ACTIVE'),
(4, 'MacBook Pro 14"', 'Apple M3 Pro Chip, 18GB RAM', 100.00, 20, 'http://localhost:8080/uploads/macbook.jpg', 'ACTIVE');

-- 5. SEED FILE UPLOADS
INSERT INTO file_uploads (file_name, file_url, file_type, file_size, module, target_id) VALUES
('iphone16.jpg', 'http://localhost:8080/uploads/iphone16.jpg', 'image/jpeg', 204800, 'PRODUCT', 1),
('s24.jpg', 'http://localhost:8080/uploads/s24.jpg', 'image/jpeg', 215000, 'PRODUCT', 2);