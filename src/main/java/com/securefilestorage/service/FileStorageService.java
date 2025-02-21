package com.securefilestorage.service;


import com.securefilestorage.config.AwsProperties;
import com.securefilestorage.exception.AwsServiceException;
import com.securefilestorage.exception.FileStorageException;
import com.securefilestorage.model.FileMetadata;
import com.securefilestorage.repository.FileMetadataRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DecryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptRequest;
import software.amazon.awssdk.services.kms.model.KmsException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Handling file storage operations
 * with AWS S3 and encryption using AWS KMS.
 *
 * @author Dzmitry Ivaniuta
 * @version 1.0
 * @since 2025
 */
@Service
@Slf4j
public class FileStorageService {

    /** AWS S3 client for interacting with S3 buckets */
    private final S3Client s3Client;

    /** AWS KMS client for encryption and decryption */
    private final KmsClient kmsClient;

    /** AWS SSM client for fetching configuration from Parameter Store */
    private final SsmClient ssmClient;

    /** AWS configuration properties */
    private final AwsProperties awsProperties;

    /** Repository for storing file metadata */
    private final FileMetadataRepository fileMetadataRepository;

    /** KMS Key ID used for encryption and decryption */
    private String kmsKeyId;

    public FileStorageService(final S3Client s3Client, final KmsClient kmsClient, final SsmClient ssmClient,
                              final AwsProperties awsProperties, final FileMetadataRepository fileMetadataRepository) {
        this.s3Client = s3Client;
        this.kmsClient = kmsClient;
        this.ssmClient = ssmClient;
        this.awsProperties = awsProperties;
        this.fileMetadataRepository = fileMetadataRepository;
//        this.kmsKeyId = fetchKmsKeyId();
    }

    @PostConstruct
    private void init() {
        this.kmsKeyId = fetchKmsKeyId();
    }

    /**
     * Fetches the KMS Key ID from AWS Systems Manager Parameter Store.
     *
     * @return the KMS Key ID.
     */
    private String fetchKmsKeyId() {
        try {
            GetParameterRequest parameterRequest = GetParameterRequest.builder()
                    .name("/secure-file-storage/kms-key-id")
                    .withDecryption(true)
                    .build();

            GetParameterResponse parameterResponse = ssmClient.getParameter(parameterRequest);
            log.info("Successfully fetched KMS Key ID from Parameter Store.");
            return parameterResponse.parameter().value();
        } catch (Exception e) {
            log.error("Failed to fetch KMS Key ID from Parameter Store.", e);
            throw new AwsServiceException("Failed to fetch KMS Key ID from Parameter Store.", e);
        }
    }

    /**
     * Uploads and encrypts a file to AWS S3 and stores metadata.
     *
     * @param file the file to upload.
     * @return the unique filename stored in S3.
     */
    public String uploadFile(MultipartFile file) {
        String uniqueUUID = UUID.randomUUID().toString();
        String uniqueFileName = uniqueUUID + "_" + file.getOriginalFilename();
        try {
            log.info("Starting file upload: {}", file.getOriginalFilename());
            SdkBytes encryptedData = encryptFile(file.getBytes(), kmsKeyId);
            // Upload encrypted file to S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(awsProperties.getS3().getBucketName())
                    .key(uniqueFileName)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(encryptedData.asByteArray()));
            log.info("File uploaded to S3 bucket: {}", awsProperties.getS3().getBucketName());

            // Store file metadata
            FileMetadata metadata = new FileMetadata();
            metadata.setS3Key(uniqueUUID);
            metadata.setFileName(file.getOriginalFilename());
            metadata.setUploadedAt(LocalDateTime.now());
            metadata.setBucketName(awsProperties.getS3().getBucketName());
            fileMetadataRepository.save(metadata);

            log.info("File metadata stored successfully.");
            return uniqueFileName;
        } catch (IOException e) {
            log.error("Failed to read file data.", e);
            throw new FileStorageException("Failed to read file data.", e);
        } catch (S3Exception | KmsException e) {
            log.error("Failed to upload encrypted file to S3.", e);
            throw new AwsServiceException("Failed to upload encrypted file to S3.", e);
        }
    }

    /**
     * Downloads and decrypts a file from AWS S3.
     *
     * @param fileName the name of the file to download.
     * @return the byte array to the decrypted file.
     */
    public byte[] downloadFileAsBytes(String fileName) {
        log.info("Starting file bytes download: {}", fileName);

        // Prepare S3 get request
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(awsProperties.getS3().getBucketName())
                .key(fileName)
                .build();
        byte[] encryptedData = s3Client.getObjectAsBytes(getObjectRequest).asByteArray();


        return decryptFile(encryptedData).asByteArray();
    }

    /**
     * Downloads and decrypts a file from AWS S3.
     *
     * @param fileName the name of the file to download.
     * @return the path to the decrypted file.
     */
    public Path downloadFileAsPath(String fileName) {
        log.info("Starting file download: {}", fileName);

        // Create a temporary file for storing the downloaded content
        Path tempFile;
        try {
            tempFile = Files.createTempFile("download-", fileName);
        } catch (IOException e) {
            log.error("Failed to create a temporary file for download.", e);
            throw new FileStorageException("Failed to create a temporary file.", e);
        }

        // Prepare S3 get request
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(awsProperties.getS3().getBucketName())
                .key(fileName)
                .build();

        try (InputStream s3ObjectStream = s3Client.getObject(getObjectRequest)) {
            log.info("File {} successfully retrieved from S3.", fileName);

            // Read encrypted file content
            byte[] encryptedData = s3ObjectStream.readAllBytes();

            SdkBytes decryptedData = decryptFile(encryptedData);

            // Write decrypted content to temp file
            Files.write(tempFile, decryptedData.asByteArray(), StandardOpenOption.WRITE);
            log.info("File {} downloaded and decrypted successfully.", fileName);

            return tempFile;
        } catch (S3Exception e) {
            log.error("S3 error while downloading file: {}", fileName, e);
            throw new FileStorageException("Error downloading file from S3: " + fileName, e);
        } catch (KmsException e) {
            log.error("KMS decryption error for file: {}", fileName, e);
            throw new FileStorageException("Error decrypting file: " + fileName, e);
        } catch (IOException e) {
            log.error("I/O error while processing file: {}", fileName, e);
            throw new FileStorageException("I/O error processing file: " + fileName, e);
        }
    }

    /**
     * Deletes a file from AWS S3 and removes metadata.
     *
     * @param fileName the name of the file to delete.
     */
    public void deleteFile(String fileName) {
        try {
            log.info("Deleting file: {}", fileName);
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(awsProperties.getS3().getBucketName())
                    .key(fileName)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("File deleted from S3 bucket: {}", awsProperties.getS3().getBucketName());

            // Delete file metadata
            fileMetadataRepository.deleteByFileName(fileName);
            log.info("File metadata deleted successfully.");
        } catch (S3Exception e) {
            log.error("Failed to delete file from S3.", e);
            throw new AwsServiceException("Failed to delete file from S3.", e);
        }
    }

    /**
     * Encrypts file data using AWS KMS.
     *
     * @param data the json data.
     * @return encrypted data as a byte array.
     */
    private SdkBytes encryptFile(final byte[] data, final String kmsKeyId) throws IOException {
        SdkBytes plaintext = SdkBytes.fromByteArray(data);
        EncryptRequest encryptRequest = EncryptRequest.builder()
                .keyId(kmsKeyId)
                .plaintext(plaintext)
                .build();
        log.info("File encrypted successfully.");
        return kmsClient.encrypt(encryptRequest).ciphertextBlob();
    }

    /**
     * Decrypts file data using AWS KMS.
     *
     * @param encryptedData encrypted file data as a byte array.
     * @return decrypted file data as a byte array.
     */
    private SdkBytes decryptFile(byte[] encryptedData) {
        DecryptRequest decryptRequest = DecryptRequest.builder()
                .ciphertextBlob(SdkBytes.fromByteArray(encryptedData))
                .build();
        return kmsClient.decrypt(decryptRequest).plaintext();
    }
}
