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

    private ScrubbedInput mail;
    private String captcha;

    private ForgotRecoverForm() {
    }

    public static ForgotRecoverForm newInstance() {
        return new ForgotRecoverForm();
    }

    public ScrubbedInput getMail() {
        return mail;
    }

    public void setMail(ScrubbedInput mail) {
        this.mail = mail;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
