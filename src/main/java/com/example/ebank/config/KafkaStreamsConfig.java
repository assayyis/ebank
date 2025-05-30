package com.example.ebank.config;

import com.example.ebank.model.Transaction;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class KafkaStreamsConfig {

    @Bean
    public KStream<String, Transaction> transactionStream(StreamsBuilder builder) {
        KStream<String, Transaction> stream = builder.stream("transactions");

        // Example: Filter for a given customer - will be done in service with parameters

        // Optionally materialize aggregations per customer-month

        return stream;
    }
}

