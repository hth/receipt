package com.tholix.web.form;

/**
 * User: hitender
 * Date: 5/31/13
 * Time: 1:19 AM
 */
public class ForgotRecoverForm {
    private String emailId;
    private String captcha;

    public static ForgotRecoverForm newInstance() {
        return new ForgotRecoverForm();
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
