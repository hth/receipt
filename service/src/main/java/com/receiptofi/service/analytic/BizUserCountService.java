package com.receiptofi.service.analytic;

import com.receiptofi.domain.analytic.BizUserCountEntity;
import com.receiptofi.repository.analytic.BizUserCountManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: hitender
 * Date: 6/3/16 3:40 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class BizUserCountService {

    private BizUserCountManager bizUserCountManager;

    @Autowired
    public BizUserCountService(BizUserCountManager bizUserCountManager) {
        this.bizUserCountManager = bizUserCountManager;
    }

    public BizUserCountEntity findBy(String bizId) {
        return bizUserCountManager.findBy(bizId);
    }
}
