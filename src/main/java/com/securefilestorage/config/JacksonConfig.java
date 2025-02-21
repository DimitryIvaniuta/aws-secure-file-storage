package com.securefilestorage.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Configuration class for Jackson to register the JavaTimeModule
 * and disable writing dates as timestamps.
 */
@Configuration
public class JacksonConfig {
/*
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            // Register the JavaTimeModule to handle Java 8 Date/Time types
            builder.modules(new JavaTimeModule());
            // Disable serialization of dates as timestamps
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        };
    }*/

    /**
     * Defines an ObjectMapper bean that registers the JavaTimeModule
     * to handle Java 8 date/time types and disables writing dates as timestamps.
     *
     * @return a configured ObjectMapper.
     */

    @Bean
    public ObjectMapper objectMapper() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        // Register the JavaTimeModule to handle Java 8 date/time types (including ZonedDateTime)
        builder.modules(new JavaTimeModule());
        // Disable serialization of dates as timestamps
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return builder.build();
    }
}