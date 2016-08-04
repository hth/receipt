package com.receiptofi.domain.flow;

import java.io.Serializable;

/**
 * User: hitender
 * Date: 7/28/16 6:35 AM
 */
public class Register implements Serializable {

    private RegisterUser registerUser = new RegisterUser();
    private RegisterBusiness registerBusiness = new RegisterBusiness();

    public RegisterUser getRegisterUser() {
        return registerUser;
    }

    public void setRegisterUser(RegisterUser registerUser) {
        this.registerUser = registerUser;
    }

    public RegisterBusiness getRegisterBusiness() {
        return registerBusiness;
    }

    public void setRegisterBusiness(RegisterBusiness registerBusiness) {
        this.registerBusiness = registerBusiness;
    }
}
