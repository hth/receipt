package com.receiptofi.service.analytic;

import com.receiptofi.domain.analytic.UserDimensionEntity;
import com.receiptofi.repository.analytic.UserDimensionManager;
import com.receiptofi.utils.Maths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.GeoResults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

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
public class UserDimensionService {

    private UserDimensionManager userDimensionManager;

    @Autowired
    public UserDimensionService(UserDimensionManager userDimensionManager) {
        this.userDimensionManager = userDimensionManager;
    }

    public List<UserDimensionEntity> getAllStoreUsers(String storeId) {
        return userDimensionManager.getAllStoreUsers(storeId);
    }

    public int getBusinessUserCount(String bizId, int percent) {
        long count = userDimensionManager.getBusinessUserCount(bizId);
        return Maths.divide(Maths.multiply(BigDecimal.valueOf(count), percent), 100).intValue();
    }

    public GeoResults<UserDimensionEntity> findAllNonPatrons(
            double longitude,
            double latitude,
            int distributionRadius,
            String storeId,
            String countryShortName) {
        return userDimensionManager.findAllNonPatrons(longitude, latitude, distributionRadius, storeId, countryShortName);
    }

    public Set<String> findUserAssociatedAllDistinctBizStr(String rid) {
        return userDimensionManager.findUserAssociatedAllDistinctBizStr(rid);
    }

    public Set<String> findUserAssociatedBizName(String bizName, String rid) {
        return userDimensionManager.findUserAssociatedBizName(bizName, rid);
    }
}
