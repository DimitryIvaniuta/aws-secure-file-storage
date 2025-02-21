package com.securefilestorage.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * JPA Entity for storing metadata of uploaded files.
 */
@Entity
@Table(name = "file_metadata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileMetadata {

    /**
     * Primary key - Auto-generated ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AWS_STORAGE_UNIQUE_ID")
    @SequenceGenerator(name = "AWS_STORAGE_UNIQUE_ID", sequenceName = "AWS_STORAGE_UNIQUE_ID", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Bucket Name.
     */
    @Column(name = "bucket_name", nullable = false)
    private String bucketName;

    /**
     * Original file name uploaded by the user.
     */
    @Column(name = "file_name", nullable = false)
    private String fileName;

    /**
     * The S3 key (unique identifier) for the file in AWS S3.
     */
    @Column(name="s3_key", nullable = false, unique = true)
    private String s3Key;

    /**
     * File size in bytes.
     */
    @Column(name="file_size", nullable = false)
    private Long fileSize;

    /**
     * Username of the uploader.
     */
    @Column(name="uploaded_by")
    private String uploadedBy;

    /**
     * Date and time when the file was uploaded.
     */

    @Column(name="upload_date", nullable = false)
    private LocalDateTime uploadedAt;
}
