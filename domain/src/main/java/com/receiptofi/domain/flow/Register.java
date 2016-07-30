package com.receiptofi.domain.flow;

import com.receiptofi.domain.types.BusinessTypeEnum;

import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.List;

/**
 * User: hitender
 * Date: 7/28/16 6:35 AM
 */
class Register implements Serializable {
    String rid;
    String email;
    String firstName;
    String lastName;
    String address;
    String countryShortName;
    String phone;
    boolean emailValidated;
    String businessName;

    /** Business types are initialized in flow. Why? Show off. */
    List<BusinessTypeEnum> businessTypes;
    String businessAddress;
    String businessPhone;
    String businessCountryShortName;

    @Transient
    private List<BusinessTypeEnum> availableBusinessTypes;

    public List<BusinessTypeEnum> getAvailableBusinessTypes() {
        return availableBusinessTypes;
    }

    public void setAvailableBusinessTypes(List<BusinessTypeEnum> availableBusinessTypes) {
        this.availableBusinessTypes = availableBusinessTypes;
    }
}
