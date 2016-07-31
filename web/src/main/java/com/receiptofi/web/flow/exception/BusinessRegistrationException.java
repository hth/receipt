package com.receiptofi.web.flow.exception;

/**
 * User: hitender
 * Date: 7/27/16 4:48 PM
 */
public class BusinessRegistrationException extends RuntimeException {
    public BusinessRegistrationException(String message) {
        super(message);
    }

    public BusinessRegistrationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
