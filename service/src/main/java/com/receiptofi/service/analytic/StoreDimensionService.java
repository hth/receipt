package com.receiptofi.service.analytic;

import com.receiptofi.repository.analytic.StoreDimensionManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: hitender
 * Date: 7/2/16 4:41 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class StoreDimensionService {

    private StoreDimensionManager storeDimensionManager;

    @Autowired
    public StoreDimensionService(StoreDimensionManager storeDimensionManager) {
        this.storeDimensionManager = storeDimensionManager;
    }
}
