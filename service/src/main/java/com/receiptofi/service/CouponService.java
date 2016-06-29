package com.receiptofi.service;

import com.receiptofi.domain.CouponEntity;
import com.receiptofi.repository.CouponManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: hitender
 * Date: 4/27/16 11:59 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class CouponService {

    private int limit;
    private CouponManager couponManager;

    @Autowired
    public CouponService(
            @Value("${limit:10}")
            int limit,

            CouponManager couponManager
    ) {
        this.limit = limit;
        this.couponManager = couponManager;
    }

    public List<CouponEntity> findCouponToUpload() {
        return couponManager.findCouponToUpload(limit);
    }

    /**
     * Update path of image location.
     *
     * @param id
     * @param imagePathOnCloud
     */
    public void cloudUploadSuccessful(String id, String imagePathOnCloud) {
        couponManager.cloudUploadSuccessful(id, imagePathOnCloud);
    }
}
