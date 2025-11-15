package com.example.Connect_Money_API.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionRequest {
    @NotBlank(message = "Transaction ID is required")
    private String id;

    @NotBlank(message = "Type is required")
    private String type;

    @NotBlank(message = "Status is required")
    private String status;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0000001", message = "Amount must be greater than 0") //Ay raqm sogayer :)
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "Card UID is required")
    private String cardUid;

    @NotNull(message = "Created date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;
}
