package com.receiptofi.web.form.admin;

import com.receiptofi.utils.ScrubbedInput;

/**
 * User: hitender
 * Date: 11/27/16 5:29 PM
 */
public class NotificationSendForm {

    private ScrubbedInput rid;
    private ScrubbedInput message;

    private String errorMessage;
    private String successMessage;

    private NotificationSendForm() {
    }

    public static NotificationSendForm newInstance() {
        return new NotificationSendForm();
    }

    public ScrubbedInput getRid() {
        return rid;
    }

    public void setRid(ScrubbedInput rid) {
        this.rid = rid;
    }

    public ScrubbedInput getMessage() {
        return message;
    }

    public void setMessage(ScrubbedInput message) {
        this.message = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }
}
