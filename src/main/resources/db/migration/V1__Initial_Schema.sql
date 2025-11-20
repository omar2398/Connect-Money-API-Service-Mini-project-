-- ===============================================
-- Connect Money API - Initial Database Schema
-- ===============================================

-- Table: client
CREATE TABLE IF NOT EXISTS client (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id VARCHAR(255) NOT NULL UNIQUE,
    client_secret VARCHAR(255) NOT NULL,
    active BIT(1) DEFAULT TRUE,
    failed_attempts INT NOT NULL DEFAULT 0,
    locked_until DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX idx_clientId (client_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: transactions
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id VARCHAR(255) NOT NULL UNIQUE,
    type VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    amount DECIMAL(38,2) NOT NULL,
    currency VARCHAR(255) NOT NULL,
    card_uid VARCHAR(255) NOT NULL,
    transaction_date DATE NOT NULL,
    client_id VARCHAR(255) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX idx_transactionId (transaction_id),
    INDEX idx_cardUid (card_uid),
    INDEX idx_clientId_txn (client_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
