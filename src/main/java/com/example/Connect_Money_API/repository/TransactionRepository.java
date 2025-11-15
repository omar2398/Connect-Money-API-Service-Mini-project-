package com.example.Connect_Money_API.repository;

import com.example.Connect_Money_API.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findTransactionByTransactionId(String transactionId);
}
