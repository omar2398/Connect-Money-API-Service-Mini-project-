package com.example.Connect_Money_API.Service;

import com.example.Connect_Money_API.dto.TransactionRequest;
import com.example.Connect_Money_API.model.IdempotencyKey;
import com.example.Connect_Money_API.model.Transaction;
import com.example.Connect_Money_API.repository.IdempotencyKeyRepository;
import com.example.Connect_Money_API.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;

    @Transactional
    public void transactionProcess(String idempotencyKey, TransactionRequest request){
        var existingKey = idempotencyKeyRepository.findIdempotencyKeyByIdempotencyKeyId(idempotencyKey);

        //To avoid duplication of the transaction
        if(existingKey.isPresent() && existingKey.get().getProcessed()){
            log.info("The key is duplicated so the process is done before");
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String clientId = auth.getName(); //this is linked to the data we set in the Auth filter

        Transaction transaction = Transaction
                .builder()
                .transactionId(request.getId())
                .type(request.getType())
                .status(request.getStatus())
                .amount(request.getAmount())
                .cardUid(request.getCardUid())
                .transactionDate(request.getCreatedAt())
                .clientId(clientId)
                .currency(request.getCurrency())
                .build();

        transactionRepository.save(transaction);
        // 3shan nestakhdem nafs el key and avoid the idmpotency key duplication
        IdempotencyKey key = existingKey.orElse(
                IdempotencyKey.builder()
                        .transactionId(request.getId())
                        .idempotencyKey(idempotencyKey)
                        .build()
        );

        key.setProcessed(true);
        idempotencyKeyRepository.save(key);

        log.info("Transaction is successfully doneeeeee :D {} for the user {}", request.getId(), clientId);
    }
}
