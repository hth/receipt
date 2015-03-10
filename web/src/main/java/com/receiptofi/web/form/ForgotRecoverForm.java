package com.receiptofi.web.form;

import com.receiptofi.utils.ScrubbedInput;

/**
 * User: hitender
 * Date: 5/31/13
 * Time: 1:19 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class ForgotRecoverForm {

    private ScrubbedInput emailId;
    private String captcha;

    private ForgotRecoverForm() {
    }

    public static ForgotRecoverForm newInstance() {
        return new ForgotRecoverForm();
    }

    public String getEmailId() {
        return emailId.getText().toLowerCase();
    }

    public void setEmailId(String emailId) {
        this.emailId = new ScrubbedInput(emailId);
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
