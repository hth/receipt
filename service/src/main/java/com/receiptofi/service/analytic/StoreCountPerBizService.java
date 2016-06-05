package com.receiptofi.service.analytic;

import com.receiptofi.repository.analytic.StoreCountPerBizManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: hitender
 * Date: 6/4/16 11:22 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class StoreCountPerBizService {

    private StoreCountPerBizManager storeCountPerBizManager;

    @Autowired
    public StoreCountPerBizService(StoreCountPerBizManager storeCountPerBizManager) {
        this.storeCountPerBizManager = storeCountPerBizManager;
    }

    public long getNumberOfStoresForBiz(String bizId) {
        return storeCountPerBizManager.findOne(bizId).getStoreCount();
    }
}
