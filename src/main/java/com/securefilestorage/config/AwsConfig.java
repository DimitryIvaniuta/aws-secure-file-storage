package com.securefilestorage.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.ssm.SsmClient;

/**
 * AWS clients configuration for S3, KMS, and Secrets Manager.
 *
 * @author Dzmitry Ivaniuta
 * @version 1.0
 * @since 2025
 */
@Configuration
@AllArgsConstructor
@Slf4j
public class AwsConfig {

    /** AWS configuration properties */
    private final AwsProperties awsProperties;

    /**
     * Configures and provides an S3Client bean for interacting with Amazon S3.
     *
     * @return a configured S3Client instance.
     */
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(awsProperties.getS3().getRegion()))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    /**
     * Configures and provides a KmsClient bean for interacting with AWS KMS.
     *
     * @return a configured {@link KmsClient} instance.
     */
    @Bean
    public KmsClient kmsClient() {
        log.info("AwsConfig:kmsClient load");
        return KmsClient.builder()
                .region(Region.of(awsProperties.getS3().getRegion()))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Bean
    public SsmClient ssmClient() {
        return SsmClient.builder()
                .region(Region.of(awsProperties.getS3().getRegion()))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

}