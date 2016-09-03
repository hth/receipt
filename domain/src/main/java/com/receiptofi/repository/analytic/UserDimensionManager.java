package com.receiptofi.repository.analytic;

import com.receiptofi.domain.analytic.UserDimensionEntity;
import com.receiptofi.repository.RepositoryManager;

import org.springframework.data.geo.GeoResults;

import java.util.List;
import java.util.Set;

/**
 * User: hitender
 * Date: 7/2/16 3:39 PM
 */
public interface UserDimensionManager extends RepositoryManager<UserDimensionEntity> {

    List<UserDimensionEntity> getAllStoreUsers(String storeId);

    long getBusinessUserCount(String bizId);

    GeoResults<UserDimensionEntity> findAllNonPatrons(
            double longitude,
            double latitude,
            int distributionRadius,
            String storeId,
            String countryShortName);

    /**
     * Find all the business name visited by user with RID.
     *
     * @param rid
     * @return
     */
    Set<String> findUserAssociatedAllDistinctBizStr(String rid);

    /**
     * Find matching business name visited by user with RID.
     *
     * @param bizName
     * @param rid
     * @return
     */
    Set<String> findUserAssociatedBizName(String bizName, String rid);
}
