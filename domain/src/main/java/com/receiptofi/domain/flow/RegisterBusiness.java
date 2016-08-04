package com.receiptofi.domain.flow;

import com.receiptofi.domain.BusinessUserEntity;
import com.receiptofi.domain.types.BusinessTypeEnum;
import com.receiptofi.utils.CommonUtil;

import org.apache.commons.lang3.StringUtils;

import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.List;

/**
 * User: hitender
 * Date: 8/2/16 8:19 PM
 */
public class RegisterBusiness implements Serializable {

    private String businessName;
    /** Business types are initialized in flow. Why? Show off. */
    private List<BusinessTypeEnum> businessTypes;
    private String businessAddress;
    private String businessPhone;
    private String businessCountryShortName;
    private BusinessUserEntity businessUser;

    @Transient
    private List<BusinessTypeEnum> availableBusinessTypes;

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
        if (StringUtils.isNotBlank(businessPhone)) {
            return CommonUtil.phoneFormatter(businessPhone, businessCountryShortName);
        } else {
            return businessPhone;
        }
    }

    @Transient
    public String getBusinessPhoneNotFormatted() {
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

    public BusinessUserEntity getBusinessUser() {
        return businessUser;
    }

    public void setBusinessUser(BusinessUserEntity businessUser) {
        this.businessUser = businessUser;
    }

    public List<BusinessTypeEnum> getAvailableBusinessTypes() {
        return availableBusinessTypes;
    }

    public void setAvailableBusinessTypes(List<BusinessTypeEnum> availableBusinessTypes) {
        this.availableBusinessTypes = availableBusinessTypes;
    }
}
