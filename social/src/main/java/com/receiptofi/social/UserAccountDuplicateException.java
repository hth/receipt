package com.receiptofi.social;

/**
 * User: hitender
 * Date: 9/28/14 12:31 PM
 */
public class UserAccountDuplicateException extends RuntimeException {

    public UserAccountDuplicateException(String message) {
        super(message);
    }

    public UserAccountDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }
}
