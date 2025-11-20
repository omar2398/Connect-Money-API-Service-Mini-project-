-- ===============================================
-- Connect Money API - Sample Data
-- ===============================================

-- Insert sample clients
INSERT INTO client (client_id, client_secret, active, failed_attempts)
VALUES
('test_client', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYNH5RlkYou', TRUE, 0),
('demo_client', '$2a$12$Xp8JvH2KwP9h5L3mN7qR0O1kL9mN5pQ7rS3tU5vW7xY9zA1bC2dE3', TRUE, 0);

-- Insert sample transactions
INSERT INTO transactions (
    transaction_id, type, status, amount, currency, card_uid, transaction_date, client_id, created_at
) VALUES
('txn_sample_001','TRANSFER','COMPLETED',100.50,'EGP','100003145552','2025-11-01','test_client',NOW()),
('txn_sample_002','PAYMENT','COMPLETED',250.75,'EGP','100003145552','2025-11-02','test_client',NOW()),
('txn_sample_003','WITHDRAWAL','PENDING',500.00,'EGP','100003145553','2025-11-03','demo_client',NOW());
