package com.receiptofi.domain.flow;

import com.receiptofi.domain.InviteEntity;
import com.receiptofi.domain.types.BusinessTypeEnum;
import com.receiptofi.utils.CommonUtil;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.List;

/**
 * User: hitender
 * Date: 7/27/16 5:29 PM
 */
public class BusinessRegistration extends Register implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessRegistration.class);
    private static final long serialVersionUID = -6047892968409443583L;

    private String birthday;
    private String password;
    private boolean accountExists;
    private boolean acceptsAgreement;
    private boolean registrationTurnedOn;

    private BusinessRegistration(InviteEntity invite, boolean registrationTurnedOn) {
        this.rid = invite.getInvited().getReceiptUserId();
        this.email = invite.getInvited().getEmail();
        this.emailValidated = true;
        this.registrationTurnedOn = registrationTurnedOn;
        this.accountExists = false;
    }

    public static BusinessRegistration newInstance(InviteEntity invite, boolean registrationTurnedOn) {
        return new BusinessRegistration(invite, registrationTurnedOn);
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public boolean isRegistrationTurnedOn() {
        return registrationTurnedOn;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public void setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
    }

    public String getPhone() {
        if (StringUtils.isNotBlank(phone)) {
            return CommonUtil.phoneFormatter(phone, countryShortName);
        } else {
            return phone;
        }
    }

    @Transient
    public String getPhoneNotFormatted() {
        return phone;
    }

    public BusinessRegistration setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public boolean isEmailValidated() {
        return emailValidated;
    }

    public void setEmailValidated(boolean emailValidated) {
        this.emailValidated = emailValidated;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public List<BusinessTypeEnum> getBusinessTypes() {
        return businessTypes;
    }

    public void setBusinessTypes(List<BusinessTypeEnum> businessTypes) {
        this.businessTypes = businessTypes;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getBusinessPhone() {
        return businessPhone;
    }

    public void setBusinessPhone(String businessPhone) {
        this.businessPhone = businessPhone;
    }

    public String getBusinessCountryShortName() {
        return businessCountryShortName;
    }

    public void setBusinessCountryShortName(String businessCountryShortName) {
        this.businessCountryShortName = businessCountryShortName;
    }
}
