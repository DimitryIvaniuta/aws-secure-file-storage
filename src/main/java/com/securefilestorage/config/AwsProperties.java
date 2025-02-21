package com.securefilestorage.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to map AWS-related properties from application.yml.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "aws")
public class AwsProperties {

    private S3 s3 = new S3();

    @Getter
    @Setter
    public static class S3 {

        private String bucketName;

        private String region;

        private String awsSecretName;

        private String dbSecretName;

    }

}
