package com.tholix.web.form;

/**
 * User: hitender
 * Date: 6/4/13
 * Time: 1:48 AM
 */
public class ForgotAuthenticateForm {

    private String password;
    private String passwordSecond;
    private String userProfileId;
    private String authenticationKey;

    private ForgotAuthenticateForm() { }

    public static ForgotAuthenticateForm newInstance() {
        return new ForgotAuthenticateForm();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordSecond() {
        return passwordSecond;
    }

    public void setPasswordSecond(String passwordSecond) {
        this.passwordSecond = passwordSecond;
    }

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }

    public String getAuthenticationKey() {
        return authenticationKey;
    }

    public void setAuthenticationKey(String authenticationKey) {
        this.authenticationKey = authenticationKey;
    }

    public boolean isEqual() {
        return password.equals(this.passwordSecond);
    }
}
