package com.example.Connect_Money_API.repository;

import com.example.Connect_Money_API.model.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, Long> {
    Optional<IdempotencyKey> findIdempotencyKeyByIdempotencyKeyId(String idempotencyKeyId);
}
