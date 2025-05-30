package com.example.ebank;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import com.example.ebank.service.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ExchangeRateServiceTest {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Test
    public void testRedisExchangeRateCaching() {
        Double firstCall = exchangeRateService.getExchangeRates("EUR", LocalDate.of(2024, 10, 1)).get("USD");
        Double secondCall = exchangeRateService.getExchangeRates("EUR", LocalDate.of(2024, 10, 1)).get("USD");

        assertEquals(firstCall, secondCall);
    }
}
