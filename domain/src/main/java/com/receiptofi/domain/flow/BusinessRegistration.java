package com.receiptofi.domain.flow;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.BusinessUserEntity;
import com.receiptofi.utils.CommonUtil;

import java.io.Serializable;

/**
 * User: hitender
 * Date: 5/20/16 8:24 AM
 */
public class BusinessRegistration implements Serializable {

    private String rid;
    private String email;
    private String firstName;
    private String lastName;
    private String address;
    private String countryShortName;
    private String phone;
    private boolean emailValidated;
    private BizNameEntity bizName;
    private String businessAddress;
    private String businessPhone;
    private String businessCountryShortName;
    private BusinessUserEntity businessUser;

    private BusinessRegistration(BusinessUserEntity businessUser, BizStoreEntity bizStore) {
        this.rid = businessUser.getReceiptUserId();
        this.businessUser = businessUser;
        if (null != businessUser.getBizName()) {
            this.bizName = businessUser.getBizName();
        } else {
            this.bizName = BizNameEntity.newInstance();
        }

        if (null != bizStore) {
            this.businessAddress = bizStore.getAddress();
            this.businessPhone = bizStore.getPhone();
            this.businessCountryShortName = bizStore.getCountryShortName();
        }
    }

    public static BusinessRegistration newInstance(BusinessUserEntity businessUser, BizStoreEntity bizStore) {
        return new BusinessRegistration(businessUser, bizStore);
    }

    public String getRid() {
        return rid;
    }

    public String getEmail() {
        return email;
    }

    public BusinessRegistration setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public BusinessRegistration setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public BusinessRegistration setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public BusinessRegistration setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public BusinessRegistration setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public BusinessRegistration setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public BusinessUserEntity getBusinessUser() {
        return businessUser;
    }

    public boolean isEmailValidated() {
        return emailValidated;
    }

    public BusinessRegistration setEmailValidated(boolean emailValidated) {
        this.emailValidated = emailValidated;
        return this;
    }

    public BizNameEntity getBizName() {
        return bizName;
    }

    public void setBizName(BizNameEntity bizName) {
        this.bizName = bizName;
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

    public String getPhoneFormatted() {
        return CommonUtil.phoneFormatter(phone, countryShortName);
    }

    public String getBusinessPhoneFormatted() {
        return CommonUtil.phoneFormatter(businessPhone, businessCountryShortName);
    }
}
