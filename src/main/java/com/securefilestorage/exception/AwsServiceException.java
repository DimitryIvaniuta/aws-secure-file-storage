package com.securefilestorage.exception;

/**
 * Exception thrown when an error occurs while interacting with AWS services.
 */
public class AwsServiceException extends RuntimeException {

    /**
     * Constructs a new AwsServiceException with the specified detail message.
     *
     * @param message the detail message.
     */
    public AwsServiceException(String message) {
        super(message);
    }

    /**
     * Constructs a new AwsServiceException with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause of the exception.
     */
    public AwsServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
