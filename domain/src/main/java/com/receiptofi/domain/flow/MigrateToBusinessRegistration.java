package com.receiptofi.domain.flow;

import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.BusinessUserEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Migrate from User account to any business account.
 *
 * User: hitender
 * Date: 5/20/16 8:24 AM
 */
public class MigrateToBusinessRegistration extends Register implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(MigrateToBusinessRegistration.class);
    private static final long serialVersionUID = -6047892968409443583L;

    private MigrateToBusinessRegistration(BusinessUserEntity businessUser, BizStoreEntity bizStore) {
        getRegisterBusiness().setBusinessUser(businessUser);
        getRegisterUser().setRid(businessUser.getReceiptUserId());
        if (null != businessUser.getBizName()) {
            getRegisterBusiness().setBusinessName(businessUser.getBizName().getBusinessName());
            getRegisterBusiness().setBusinessTypes(businessUser.getBizName().getBusinessTypes());
        }

        if (null != bizStore) {
            getRegisterBusiness().setBusinessAddress(bizStore.getAddress());
            getRegisterBusiness().setBusinessPhone(bizStore.getPhone());
            getRegisterBusiness().setBusinessCountryShortName(bizStore.getCountryShortName());
        }
    }

    public static MigrateToBusinessRegistration newInstance(BusinessUserEntity businessUser, BizStoreEntity bizStore) {
        return new MigrateToBusinessRegistration(businessUser, bizStore);
    }
}
