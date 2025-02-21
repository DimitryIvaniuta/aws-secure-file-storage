package com.securefilestorage.config;

import com.securefilestorage.service.AwsSecretsManagerService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DatabaseConfig {

    private final AwsSecretsManagerService awsSecretsService;
    private final Environment environment;

    @PostConstruct
    public void loadDbCredentials() {
        Map<String, String> dbCredentials = awsSecretsService.loadDbCredentials();

        if (!dbCredentials.isEmpty()) {
            System.setProperty("DB_HOST", dbCredentials.get("host"));
            System.setProperty("DB_PORT", dbCredentials.get("port"));
            System.setProperty("DB_NAME", dbCredentials.get("dbname"));
            System.setProperty("DB_USERNAME", dbCredentials.get("username"));
            System.setProperty("DB_PASSWORD", dbCredentials.get("password"));

            log.info("Database credentials loaded from AWS Secrets Manager.");
        } else {
            log.error("Failed to load database credentials from AWS Secrets Manager.");
        }
    }

}
