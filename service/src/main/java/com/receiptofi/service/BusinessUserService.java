package com.receiptofi.service;

import com.receiptofi.domain.BusinessUserEntity;
import com.receiptofi.domain.types.BusinessUserRegistrationStatusEnum;
import com.receiptofi.domain.types.UserLevelEnum;
import com.receiptofi.repository.BusinessUserManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: hitender
 * Date: 5/16/16 3:42 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class BusinessUserService {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessUserService.class);

    private BusinessUserManager businessUserManager;

    @Autowired
    public BusinessUserService(BusinessUserManager businessUserManager) {
        this.businessUserManager = businessUserManager;
    }

    /**
     * Create, update business user.
     *
     * @param rid
     * @param userLevel
     * @param active
     */
    void saveUpdateBusinessUser(String rid, UserLevelEnum userLevel, boolean active) {
        BusinessUserEntity businessUser = businessUserManager.findByRid(rid);
        switch (userLevel) {
            case BUSINESS_SMALL:
            case BUSINESS_LARGE:
                if (null == businessUser) {
                    businessUser = BusinessUserEntity.newInstance(rid);
                }

                if (active) {
                    businessUser.active();
                } else {
                    businessUser.inActive();
                }

                if (!businessUser.isDeleted()) {
                    save(businessUser);
                }
                break;
            default:
                if (null != businessUser && !businessUser.isDeleted()) {
                    businessUser.inActive();
                    save(businessUser);
                }
                break;
        }
    }

    public BusinessUserEntity findBusinessUser(String rid) {
        return businessUserManager.findBusinessUser(rid);
    }

    public void save(BusinessUserEntity businessUser) {
        businessUserManager.save(businessUser);
    }
}
