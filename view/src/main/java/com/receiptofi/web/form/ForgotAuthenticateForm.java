package com.receiptofi.web.form;

/**
 * User: hitender
 * Date: 6/4/13
 * Time: 1:48 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class ForgotAuthenticateForm {

    private String password;
    private String passwordSecond;
    private String receiptUserId;
    private String authenticationKey;

    private ForgotAuthenticateForm() {
    }

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

    public String getReceiptUserId() {
        return receiptUserId;
    }

    public ForgotAuthenticateForm setReceiptUserId(String receiptUserId) {
        this.receiptUserId = receiptUserId;
        return this;
    }

    public String getAuthenticationKey() {
        return authenticationKey;
    }

    public ForgotAuthenticateForm setAuthenticationKey(String authenticationKey) {
        this.authenticationKey = authenticationKey;
        return this;
    }

    public boolean isEqual() {
        return password.equals(this.passwordSecond);
    }
}
