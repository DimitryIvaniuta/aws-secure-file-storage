package com.securefilestorage.controller;

import com.securefilestorage.exception.FileNotFoundException;
import com.securefilestorage.service.FileStorageService;
import com.securefilestorage.exception.FileStorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * REST Controller for handling file storage operations with AWS S3 and KMS encryption.
 * Provides endpoints to upload, download, and delete files securely.*
 *
 * @author Dzmitry Ivaniuta
 * @version 1.0
 * @since 2024
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileStorageController {

    private final FileStorageService fileStorageService;

    /**
     * Uploads a file to AWS S3 with encryption using AWS KMS.
     *
     * @param file the file to upload.
     * @return a message with the uploaded file name.
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("Received file upload request: {}", file.getOriginalFilename());
        String storedFileName = fileStorageService.uploadFile(file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("File uploaded successfully with name: " + storedFileName);
    }

    /**
     * Download file by name as bytes[] and decrypts a file from AWS S3.
     *
     * @param filename the name of the file to download.
     * @return the decrypted file as a byte array.
     */
    @GetMapping("/download/bytes/{filename}")
    public ResponseEntity<byte[]> downloadFileAsBytes(@PathVariable String filename) {
        log.info("Received file download request: {}", filename);
            byte[] fileData = fileStorageService.downloadFileAsBytes(filename);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);

            log.info("File '{}' downloaded successfully.", filename);
            return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
    }

    /**
     * Download file by name as Path and decrypts a file from AWS S3.
     *
     * @param filename the name of the file to download.
     * @return the decrypted file as a byte array.
     */
    @GetMapping("/download/path/{filename}")
    public ResponseEntity<Resource> downloadFileAsPath(@PathVariable String filename) {
        log.info("Received file download request: {}", filename);
        Path filePath = fileStorageService.downloadFileAsPath(filename);
        if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
            throw new FileNotFoundException("File not found or not readable: " + filename);
        }
        try {
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(filePath));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentLength(Files.size(filePath))
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException e) {
            log.error("Error reading file: {}", filename, e);
            throw new FileStorageException("Failed to read file: " + filename, e);
        }
    }

    /**
     * Retrieves the list of uploaded files from AWS S3.
     *
     * @return a list of file names.
     */
    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles() {
        log.info("Received request to list files");
        List<String> files = fileStorageService.listFiles();
        return ResponseEntity.ok(files);
    }

    /**
     * Deletes a file from AWS S3.
     *
     * @param filename the name of the file to delete.
     * @return a success or failure message.
     */
    @DeleteMapping("/delete/{filename}")
    public ResponseEntity<String> deleteFile(@PathVariable String filename) {
        try {
            fileStorageService.deleteFile(filename);
            log.info("File '{}' deleted successfully.", filename);
            return ResponseEntity.ok("File deleted successfully: " + filename);
        } catch (Exception e) {
            log.error("Error deleting file '{}': {}", filename, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete file.");
        }
    }
}
