package com.example.Connect_Money_API.controller;

import com.example.Connect_Money_API.Service.TransactionService;
import com.example.Connect_Money_API.dto.TransactionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/transactions")
public class TransactionController {
    private final TransactionService transactionService;


    @PostMapping
    public ResponseEntity<Void> createTransaction(
            @RequestHeader("idempotency-key") String idempotencyKey,
            @Valid @RequestBody TransactionRequest request) {

        log.info("Transaction id request received {} with idempotencykey id is: {}",
                request.getId(), idempotencyKey);

        if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
            log.error("idempotencykey is missing!!");
            return ResponseEntity.badRequest().build();
        }

        transactionService.transactionProcess(idempotencyKey, request);
        return ResponseEntity.ok().build();
    }
}

