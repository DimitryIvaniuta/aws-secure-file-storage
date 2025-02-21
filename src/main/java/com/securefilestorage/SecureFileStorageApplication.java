package com.securefilestorage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main entry point for the Secure File Storage application.
 * Storing and retrieving files using AWS S3.
 * Encrypting data with AWS KMS, and managing metadata using an RDS-backed database.
 *
 * @author Dzmitry Ivaniuta
 * @version 1.0
 * @since 2025
 */
@SpringBootApplication
@EnableAsync
@ConfigurationPropertiesScan
public class SecureFileStorageApplication {

    /**
     * Start the Spring Boot application.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(SecureFileStorageApplication.class, args);
    }
}
