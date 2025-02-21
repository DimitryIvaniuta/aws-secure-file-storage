package com.securefilestorage.exception;

/**
 * Exception thrown when an error occurs during file storage operations.
 */
public class FileStorageException extends RuntimeException {

    /**
     * Constructs a new FileStorageException with the specified detail message.
     *
     * @param message the detail message.
     */
    public FileStorageException(String message) {
        super(message);
    }

    /**
     * Constructs a new FileStorageException with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause of the exception.
     */
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
