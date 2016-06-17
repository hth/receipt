package com.receiptofi.repository.analytic;

import com.receiptofi.domain.analytic.BizDimensionEntity;
import com.receiptofi.repository.RepositoryManager;

/**
 * User: hitender
 * Date: 6/8/16 4:20 PM
 */
public interface BizDimensionManager extends RepositoryManager<BizDimensionEntity> {

    BizDimensionEntity findBy(String bizId);
}
