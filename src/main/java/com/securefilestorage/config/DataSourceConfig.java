package com.securefilestorage.config;

import com.securefilestorage.service.AwsSecretsManagerService;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Configuration class that creates a Hikari DataSource based on credentials
 * stored in AWS Secrets Manager.
 */
@Configuration
public class DataSourceConfig {

    private final AwsSecretsManagerService secretsManagerService;

    /**
     * Constructor-based injection of the AwsSecretsManagerService.
     *
     * @param secretsManagerService Service that loads secrets from AWS Secrets Manager
     */
    public DataSourceConfig(AwsSecretsManagerService secretsManagerService) {
        this.secretsManagerService = secretsManagerService;
    }


    /**
     * Creates and configures a HikariDataSource bean using database credentials
     * fetched from AWS Secrets Manager.
     *
     * @return a DataSource instance configured for PostgreSQL
     */
    @Bean
    public DataSource dataSource() {
        // Load the database credentials from AWS Secrets Manager
        Map<String, String> dbSecrets = secretsManagerService.loadDbCredentials();

        // Extract values from the secrets map (with optional defaults)
        String username = dbSecrets.getOrDefault("username", "admin");
        String password = dbSecrets.getOrDefault("password", "securepassword");
        String host = dbSecrets.getOrDefault("host", "localhost");
        String port = dbSecrets.getOrDefault("port", "5432");
        String dbName = dbSecrets.getOrDefault("dbname", "secure_db");

        // Construct the JDBC URL for PostgreSQL
        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName);

        // Create a new HikariDataSource and configure it
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(jdbcUrl);
        hikariDataSource.setUsername(username);
        hikariDataSource.setPassword(password);
        hikariDataSource.setDriverClassName("org.postgresql.Driver");

        // Optional: Tune HikariCP settings as needed
        hikariDataSource.setMaximumPoolSize(10);

        return hikariDataSource;
    }
}