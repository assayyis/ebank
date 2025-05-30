package com.example.ebank.service;

import com.example.ebank.model.Transaction;
import com.example.ebank.model.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    @Autowired
    public ExchangeRateService exchangeRateService;

    public List<Transaction> allTransactions;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public TransactionResponse getTransactions(
            String customerId,
            int year,
            int month,
            int pageNumber,
            int pageSize) {

        List<Transaction> filtered = allTransactions.stream()
                .filter(t -> t.getCustomerId().equals(customerId))
                .filter(t -> {
                    LocalDate date = LocalDate.parse(t.getValueDate(), formatter);
                    return date.getYear() == year && date.getMonthValue() == month;
                })
                .collect(Collectors.toList());

        long totalTransactions = filtered.size();

        int fromIndex = Math.min(pageNumber * pageSize, filtered.size());
        int toIndex = Math.min(fromIndex + pageSize, filtered.size());
        List<Transaction> pageContent = filtered.subList(fromIndex, toIndex);


        BigDecimal totalCredit = pageContent.stream()
                .filter(t -> t.getAmount().compareTo(BigDecimal.ZERO) > 0)
                .map(this::rateMultiplied)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDebit = pageContent.stream()
                .filter(t -> t.getAmount().compareTo(BigDecimal.ZERO) < 0)
                .map(this::rateMultiplied)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        TransactionResponse response = new TransactionResponse();
        response.setPageNumber(pageNumber);
        response.setPageSize(pageSize);
        response.setTotalTransactions(totalTransactions);
        response.setTotalCredit(totalCredit);
        response.setTotalDebit(totalDebit);
        response.setTransactions(pageContent);

        return response;
    }

    public BigDecimal rateMultiplied(Transaction t) {
        Map<String, Double> rate = exchangeRateService.getExchangeRates("EUR", LocalDate.now());

        BigDecimal amount = t.getAmount();
        String currency = t.getCurrency();
        Double exchangeRate = rate.get(currency);

        if (exchangeRate == null) {
            exchangeRate = 1.0;
        }

        return amount.multiply(BigDecimal.valueOf(exchangeRate));
    }
}