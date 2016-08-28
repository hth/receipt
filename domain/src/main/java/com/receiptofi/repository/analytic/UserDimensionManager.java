package com.receiptofi.repository.analytic;

import com.receiptofi.domain.analytic.UserDimensionEntity;
import com.receiptofi.repository.RepositoryManager;

import org.springframework.data.geo.GeoResults;

import java.util.List;

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
}
