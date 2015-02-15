/**
 *
 */
package com.receiptofi.web.form;

import org.apache.commons.lang3.StringUtils;

/**
 * @author hitender
 * @since Dec 25, 2012 12:01:53 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class UserRegistrationForm {

    private String firstName;
    private String lastName;
    private String mail;
    private String birthday;
    private String password;
    private boolean accountExists;
    private boolean acceptsAgreement;

    private UserRegistrationForm() {
    }

    public static UserRegistrationForm newInstance() {
        return new UserRegistrationForm();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * During registration make sure all the email ids are lowered case.
     *
     * @return
     */
    public String getMail() {
        return StringUtils.lowerCase(mail);
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAccountExists() {
        return accountExists;
    }

    public void setAccountExists(boolean accountExists) {
        this.accountExists = accountExists;
    }

    public boolean isAcceptsAgreement() {
        return acceptsAgreement;
    }

    public void setAcceptsAgreement(boolean acceptsAgreement) {
        this.acceptsAgreement = acceptsAgreement;
    }

    @Override
    public String toString() {
        return "UserRegistrationForm [" +
                "firstName=" + firstName + ", " +
                "lastName=" + lastName + ", " +
                "mail=" + mail + ", " +
                "password=" + password + "]";
    }
}
