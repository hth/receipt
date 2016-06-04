package com.receiptofi.repository.analytic;

import com.receiptofi.domain.analytic.BizUserCountEntity;
import com.receiptofi.repository.RepositoryManager;

/**
 * User: hitender
 * Date: 6/3/16 3:31 PM
 */
public interface BizUserCountManager extends RepositoryManager<BizUserCountEntity> {
    BizUserCountEntity findBy(String bizId);
}
