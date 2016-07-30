package com.receiptofi.web.flow.exception;

/**
 * User: hitender
 * Date: 7/27/16 4:48 PM
 */
public class AccountantRegistrationException extends RuntimeException {
    public AccountantRegistrationException(String message) {
        super(message);
    }

    public AccountantRegistrationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
