package com.receiptofi.service;

import com.receiptofi.domain.BusinessUserEntity;
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
            //TODO add Accountant and Enterprise
            case BUSINESS:
                if (null == businessUser) {
                    businessUser = BusinessUserEntity.newInstance(rid, UserLevelEnum.BUSINESS);
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

    public boolean doesBusinessUserExists(String rid, String bizId) {
        return businessUserManager.doesBusinessUserExists(rid, bizId);
    }

    public void save(BusinessUserEntity businessUser) {
        businessUserManager.save(businessUser);
    }
}
