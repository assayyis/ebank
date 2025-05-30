package com.example.ebank;

import com.example.ebank.model.Transaction;
import com.example.ebank.model.TransactionResponse;
import com.example.ebank.service.ExchangeRateService;
import com.example.ebank.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        ExchangeRateService exchangeRateService = mock(ExchangeRateService.class);
        transactionService = new TransactionService();
        transactionService.exchangeRateService = exchangeRateService;

        transactionService.allTransactions = Arrays.asList(
                new Transaction("tx1", "cust1", new BigDecimal("100.00"), "USD", "IBAN1", "10-05-2025", "Salary"),
                new Transaction("tx2", "cust1", new BigDecimal("-50.00"), "USD", "IBAN1", "15-05-2025", "Groceries"),
                new Transaction("tx3", "cust1", new BigDecimal("200.00"), "EUR", "IBAN1", "20-05-2025", "Bonus"),
                new Transaction("tx4", "cust1", new BigDecimal("-100.00"), "GBP", "IBAN1", "25-05-2025", "Rent"),
                new Transaction("tx5", "cust2", new BigDecimal("500.00"), "USD", "IBAN2", "10-05-2025", "Transfer")
        );

        Map<String, Double> mockRates = new HashMap<>();
        mockRates.put("USD", 0.9);
        mockRates.put("EUR", 1.0);
        mockRates.put("GBP", 1.2);

        when(exchangeRateService.getExchangeRates(eq("EUR"), any(LocalDate.class)))
                .thenReturn(mockRates);
    }

    @Test
    void testGetTransactions_withCurrencyConversion() {
        TransactionResponse response = transactionService.getTransactions("cust1", 2025, 5, 0, 10);

        assertEquals(4, response.getTotalTransactions());
        assertEquals(4, response.getTransactions().size());

        assertEquals(new BigDecimal("290.000"), response.getTotalCredit());

        assertEquals(new BigDecimal("-165.000"), response.getTotalDebit());
    }

    @Test
    void testGetTransactions_pagination() {
        TransactionResponse response = transactionService.getTransactions("cust1", 2025, 5, 0, 2);

        assertEquals(0, response.getPageNumber());
        assertEquals(2, response.getPageSize());
        assertEquals(4, response.getTotalTransactions());
        assertEquals(2, response.getTransactions().size());

        assertEquals("tx1", response.getTransactions().get(0).getTransactionId());
        assertEquals("tx2", response.getTransactions().get(1).getTransactionId());
    }

    @Test
    void testGetTransactions_noResults() {
        TransactionResponse response = transactionService.getTransactions("cust1", 2025, 6, 0, 10);

        assertEquals(0, response.getTotalTransactions());
        assertTrue(response.getTransactions().isEmpty());
        assertEquals(BigDecimal.ZERO, response.getTotalCredit());
        assertEquals(BigDecimal.ZERO, response.getTotalDebit());
    }
}
