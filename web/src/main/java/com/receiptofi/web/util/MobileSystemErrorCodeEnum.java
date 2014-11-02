package com.receiptofi.web.util;

/**
 * Error code to share between APP and Mobile API
 * User: hitender
 * Date: 7/10/14 11:28 PM
 */
public enum MobileSystemErrorCodeEnum {

    USER_INPUT("100", "100"),
    API_WARNING("200", "200"),
    AUTHENTICATION("400", "Authentication denied by provider"),
    SEVERE("500", "500"),
    SEVERE_ACCOUNT_DUPLICATE("501", "Found existing user with similar login");

    private String code;
    private String message;

    private MobileSystemErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}