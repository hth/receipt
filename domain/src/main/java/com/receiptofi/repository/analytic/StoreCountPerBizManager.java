package com.receiptofi.repository.analytic;

import com.receiptofi.domain.analytic.StoreCountPerBizEntity;
import com.receiptofi.repository.RepositoryManager;

/**
 * User: hitender
 * Date: 6/4/16 11:10 AM
 */
public interface StoreCountPerBizManager extends RepositoryManager<StoreCountPerBizEntity> {

    StoreCountPerBizEntity findOne(String bizId);
}
