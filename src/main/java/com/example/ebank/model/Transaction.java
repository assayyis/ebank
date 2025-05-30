package com.example.ebank.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class Transaction {
    private String transactionId;
    private String customerId;
    private BigDecimal amount;
    private String currency;
    private String accountIban;
    private String valueDate;
    private String description;
}
