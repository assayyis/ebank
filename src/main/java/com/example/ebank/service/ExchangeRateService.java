package com.example.ebank.service;

import com.example.ebank.model.ExchangeRateResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.Map;

@Service
public class ExchangeRateService {

    private final WebClient webClient;

    @Value("${exchange.api.url}")
    private String apiUrl;

    @Value("${exchange.api.key}")
    private String apiKey;

    public ExchangeRateService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Cacheable(value = "exchangeRates", key = "#fromCurrency + '-' + #date")
    public Map<String, Double> getExchangeRates(String fromCurrency, LocalDate date) {
        String formattedDate = date.toString();

        ExchangeRateResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host(apiUrl)
                        .path("/v1/{date}")
                        .queryParam("access_key", apiKey)
                        .queryParam("base", fromCurrency)
                        .build(formattedDate))
                .retrieve()
                .bodyToMono(ExchangeRateResponse.class)
                .block();

        if (response.getRates() != null) {
            return response.getRates();
        } else {
            throw new RuntimeException("Exchange rates not available");
        }
    }
}