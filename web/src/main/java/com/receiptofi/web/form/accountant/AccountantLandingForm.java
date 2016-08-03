package com.receiptofi.web.form.accountant;

import com.receiptofi.domain.AccountantEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 8/2/16 1:26 AM
 */
public class AccountantLandingForm {
    private List<AccountantEntity> accountants;

    public List<AccountantEntity> getAccountants() {
        return accountants;
    }

    public void setAccountants(List<AccountantEntity> accountants) {
        this.accountants = accountants;
    }
}
