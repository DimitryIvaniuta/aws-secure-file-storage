package com.securefilestorage.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.securefilestorage.config.AwsProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service to interact with AWS Secrets Manager.
 */
@Slf4j
@Service
@AllArgsConstructor
public class AwsSecretsManagerService implements AwsCredentialsProvider {

    /**
     * AWS configuration properties
     */
    private final AwsProperties awsProperties;

    private final SecretsManagerClient secretsManagerClient;

    private final ObjectMapper objectMapper;

    /**
     * Load AWS credentials from Secrets Manager.
     *
     * @return AwsCredentials object
     */
    private AwsCredentials loadAwsCredentials() {
        return loadSecret(awsProperties.getS3().getAwsSecretName())
                .map(secret -> AwsBasicCredentials.create(
                        secret.get("AWS_ACCESS_KEY_ID"),
                        secret.get("AWS_SECRET_ACCESS_KEY")
                )).orElse(null);
    }

    /**
     * Load Database credentials from Secrets Manager.
     *
     * @return A map containing secret key-value pairs.
     * @throws SecretsManagerException if secret retrieval or parsing fails.
     */
    public Map<String, String> loadDbCredentials() {
        return loadSecret(awsProperties.getS3().getDbSecretName())
                .orElse(new HashMap<>());
    }

    /**
     * Fetch and parse a secret from AWS Secrets Manager.
     */
    private Optional<Map<String, String>> loadSecret(String secretName) {
        try {
            GetSecretValueRequest request = GetSecretValueRequest.builder()
                    .secretId(secretName)
                    .build();

            GetSecretValueResponse response = secretsManagerClient.getSecretValue(request);
            String secretString = response.secretString();

            JsonNode jsonNode = objectMapper.readTree(secretString);
            Map<String, String> secretMap = new HashMap<>();
            jsonNode.fields().forEachRemaining(entry -> secretMap.put(entry.getKey(), entry.getValue().asText()));

            log.info("Successfully loaded secret: {}", secretName);
            return Optional.of(secretMap);
        } catch (IOException e) {
            log.error("Failed to parse secret: {}", secretName, e);
        } catch (Exception e) {
            log.error("Error retrieving secret: {}", secretName, e);
        }
        return Optional.empty();
    }

    @Override
    public AwsCredentials resolveCredentials() {
        return loadAwsCredentials();
    }
}
