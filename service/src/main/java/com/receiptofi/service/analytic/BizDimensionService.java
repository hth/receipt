package com.receiptofi.service.analytic;

import com.receiptofi.domain.analytic.BizDimensionEntity;
import com.receiptofi.repository.analytic.BizDimensionManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: hitender
 * Date: 6/8/16 4:33 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class BizDimensionService {

    private BizDimensionManager bizDimensionManager;

    @Autowired
    public BizDimensionService(BizDimensionManager bizDimensionManager) {
        this.bizDimensionManager = bizDimensionManager;
    }

    public BizDimensionEntity findBy(String bizId) {
        return bizDimensionManager.findBy(bizId);
    }
}
