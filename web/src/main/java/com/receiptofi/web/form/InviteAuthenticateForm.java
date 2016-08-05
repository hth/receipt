package com.receiptofi.web.form;

import com.receiptofi.domain.types.UserLevelEnum;
import com.receiptofi.utils.ScrubbedInput;

/**
 * User: hitender
 * Date: 6/9/13
 * Time: 5:04 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class InviteAuthenticateForm {

    private ScrubbedInput firstName;
    private ScrubbedInput lastName;
    private ScrubbedInput mail;
    private ScrubbedInput birthday;
    private UserLevelEnum userLevel;
    private ForgotAuthenticateForm forgotAuthenticateForm;
    private boolean acceptsAgreement;

    private InviteAuthenticateForm() {
        forgotAuthenticateForm = ForgotAuthenticateForm.newInstance();
    }

    public static InviteAuthenticateForm newInstance() {
        return new InviteAuthenticateForm();
    }

    public ScrubbedInput getMail() {
        return mail;
    }

    public InviteAuthenticateForm setMail(ScrubbedInput mail) {
        this.mail = mail;
        return this;
    }

    public ScrubbedInput getFirstName() {
        return firstName;
    }

    public InviteAuthenticateForm setFirstName(ScrubbedInput firstName) {
        this.firstName = firstName;
        return this;
    }

    public ScrubbedInput getLastName() {
        return lastName;
    }

    public InviteAuthenticateForm setLastName(ScrubbedInput lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserLevelEnum getUserLevel() {
        return userLevel;
    }

    public InviteAuthenticateForm setUserLevel(UserLevelEnum userLevel) {
        this.userLevel = userLevel;
        return this;
    }

    public ForgotAuthenticateForm getForgotAuthenticateForm() {
        return forgotAuthenticateForm;
    }

    public void setForgotAuthenticateForm(ForgotAuthenticateForm forgotAuthenticateForm) {
        this.forgotAuthenticateForm = forgotAuthenticateForm;
    }

    public ScrubbedInput getBirthday() {
        return birthday;
    }

    public void setBirthday(ScrubbedInput birthday) {
        this.birthday = birthday;
    }

    public boolean isAcceptsAgreement() {
        return acceptsAgreement;
    }

    public void setAcceptsAgreement(boolean acceptsAgreement) {
        this.acceptsAgreement = acceptsAgreement;
    }
}
