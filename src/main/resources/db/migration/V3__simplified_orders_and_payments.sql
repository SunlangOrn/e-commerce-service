-- ==========================================
-- ALTER SCRIPT: SIMPLIFIED V2 (ORDERS & PAYMENTS)
-- ==========================================

-- 1. CLEANUP & STRUCTURAL UPDATES FOR USERS & PRODUCTS
ALTER TABLE users
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' AFTER role,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at;

ALTER TABLE products
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at;

-- 2. ADDRESSES (User Profile Address Book)
ALTER TABLE addresses
    ADD COLUMN is_default BOOLEAN DEFAULT FALSE AFTER postal_code,
    ADD COLUMN latitude DECIMAL(10, 8) NULL AFTER city,
    ADD COLUMN longitude DECIMAL(11, 8) NULL AFTER latitude,
    ADD COLUMN google_place_id VARCHAR(255) NULL AFTER longitude;
DROP COLUMN address_type,
    MODIFY full_name VARCHAR(100) NOT NULL,
    MODIFY phone VARCHAR(20) NOT NULL,
    MODIFY city VARCHAR(100) NOT NULL;

-- 3. ORDERS RESTRUCTURING
-- Drop old foreign key to profile addresses
ALTER TABLE orders
DROP FOREIGN KEY fk_orders_address,
    DROP COLUMN address_id;

-- Add Order Number, Address Snapshots, and Split Statuses
ALTER TABLE orders
    ADD COLUMN order_number VARCHAR(50) NOT NULL UNIQUE AFTER id,

    -- Immutable Shipping Address Snapshot
    ADD COLUMN shipping_full_name VARCHAR(100) NOT NULL AFTER user_id,
    ADD COLUMN shipping_phone VARCHAR(20) NOT NULL AFTER shipping_full_name,
    ADD COLUMN shipping_address_line VARCHAR(255) NOT NULL AFTER shipping_phone,
    ADD COLUMN shipping_city VARCHAR(100) NOT NULL AFTER shipping_address_line,
    ADD COLUMN shipping_province VARCHAR(100) AFTER shipping_city,
    ADD COLUMN shipping_postal_code VARCHAR(20) AFTER shipping_province,

    -- Financial Totals & Lifecycle
    CHANGE status order_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    ADD COLUMN payment_status VARCHAR(30) NOT NULL DEFAULT 'UNPAID' AFTER order_status,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at;

-- 4. ORDER ITEMS RESTRUCTURING
ALTER TABLE order_items
    ADD COLUMN subtotal DECIMAL(10, 2) NOT NULL AFTER price;

-- 5. PAYMENTS MODULE ENHANCEMENT
ALTER TABLE payments
    CHANGE method payment_method VARCHAR(50) NOT NULL,
    CHANGE reference_id transaction_reference VARCHAR(100) NULL,
    MODIFY status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    ADD COLUMN amount DECIMAL(10, 2) NOT NULL AFTER status,
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP AFTER paid_at;

-- 6. PERFORMANCE INDEXES
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(order_status, payment_status);
CREATE INDEX idx_payments_order ON payments(order_id);