package com.receiptofi.web.flow.exception;

/**
 * User: hitender
 * Date: 5/23/16 10:51 AM
 */
public class MigrateToBusinessRegistrationException extends RuntimeException {
    public MigrateToBusinessRegistrationException(String message) {
        super(message);
    }

    public MigrateToBusinessRegistrationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
