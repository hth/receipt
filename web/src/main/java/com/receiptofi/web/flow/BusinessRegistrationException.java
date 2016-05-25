package com.receiptofi.web.flow;

/**
 * User: hitender
 * Date: 5/23/16 10:51 AM
 */
public class BusinessRegistrationException extends RuntimeException {
    public BusinessRegistrationException(String message) {
        super(message);
    }

    public BusinessRegistrationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
