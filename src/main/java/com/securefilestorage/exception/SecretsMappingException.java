package com.securefilestorage.exception;

public class SecretsMappingException extends RuntimeException {

    /**
     * Constructs a new SecretsManagerException with a detailed message.
     *
     * @param message Detailed error message.
     */
    public SecretsMappingException(String message) {
        super(message);
    }

    /**
     * Constructs a new SecretsManagerException with a message and cause.
     *
     * @param message Detailed error message.
     * @param cause   The underlying cause of the error.
     */
    public SecretsMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}