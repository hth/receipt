package com.receiptofi.domain.flow;

import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.BusinessUserEntity;
import com.receiptofi.domain.types.BusinessTypeEnum;
import com.receiptofi.utils.CommonUtil;

import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.List;

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
    private String businessName;

    /** Business types are initialized in flow. Why? Show off. */
    private List<BusinessTypeEnum> businessTypes;
    private String businessAddress;
    private String businessPhone;
    private String businessCountryShortName;
    private BusinessUserEntity businessUser;

    @Transient
    private List<BusinessTypeEnum> availableBusinessTypes;

    private BusinessRegistration(BusinessUserEntity businessUser, BizStoreEntity bizStore) {
        this.rid = businessUser.getReceiptUserId();
        this.businessUser = businessUser;
        if (null != businessUser.getBizName()) {
            this.businessName = businessUser.getBizName().getBusinessName();
            this.businessTypes = businessUser.getBizName().getBusinessTypes();
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

    public String getPhoneFormatted() {
        return CommonUtil.phoneFormatter(phone, countryShortName);
    }

    public String getBusinessPhoneFormatted() {
        return CommonUtil.phoneFormatter(businessPhone, businessCountryShortName);
    }

    public List<BusinessTypeEnum> getAvailableBusinessTypes() {
        return availableBusinessTypes;
    }

    public void setAvailableBusinessTypes(List<BusinessTypeEnum> availableBusinessTypes) {
        this.availableBusinessTypes = availableBusinessTypes;
    }
}
