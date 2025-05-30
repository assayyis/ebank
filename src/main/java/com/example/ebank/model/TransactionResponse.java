package com.example.ebank.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TransactionResponse {
    private int pageNumber;
    private int pageSize;
    private long totalTransactions;
    private BigDecimal totalCredit;
    private BigDecimal totalDebit;
    private List<Transaction> transactions;
}
