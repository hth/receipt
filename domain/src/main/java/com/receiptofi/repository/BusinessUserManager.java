package com.receiptofi.repository;

import com.receiptofi.domain.BusinessUserEntity;

/**
 * User: hitender
 * Date: 5/16/16 3:46 PM
 */
public interface BusinessUserManager extends RepositoryManager<BusinessUserEntity> {
    /**
     * Finds business user with any status like active or inactive.
     *
     * @param rid
     * @return
     */
    BusinessUserEntity findByRid(String rid);

    /**
     * Finds active business user.
     *
     * @param rid
     * @return
     */
    BusinessUserEntity findBusinessUser(String rid);
}
