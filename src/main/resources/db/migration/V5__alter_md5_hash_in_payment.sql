ALTER TABLE payments ADD COLUMN md5_hash VARCHAR(64) NULL AFTER transaction_reference;
CREATE INDEX idx_payments_md5 ON payments(md5_hash);