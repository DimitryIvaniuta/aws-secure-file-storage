package com.securefilestorage.repository;

import com.securefilestorage.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing FileMetadata entities.
 */
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

    /**
     * Find file metadata by its S3 key.
     *
     * @param s3Key the S3 key.
     * @return Optional FileMetadata.
     */
    Optional<FileMetadata> findByS3Key(String s3Key);

    /**
     * Find file metadata by original file name.
     *
     * @param originalFileName the file name.
     * @return Optional FileMetadata.
     */
    Optional<FileMetadata> findByFileName(String originalFileName);

    /**
     * Bulk deletion of FileMetadata by name.
     *
     * @param originalFileName a file name.
     */
    void deleteByFileName(String originalFileName);

}
