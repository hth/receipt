package com.receiptofi.web.flow.exception;

/**
 * User: hitender
 * Date: 6/18/16 4:56 PM
 */
public class BusinessCampaignException extends RuntimeException {
    public BusinessCampaignException(String message) {
        super(message);
    }

    public BusinessCampaignException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
