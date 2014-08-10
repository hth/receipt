package com.receiptofi.repository;

import java.util.Date;
import java.util.List;

import com.receiptofi.domain.RecentActivityEntity;
import com.receiptofi.domain.types.RecentActivityEnum;

/**
 * Mobile APP uses this to find if there are any new updates on server before it starts fetching data. If new updates
 * since the last time it fetched, mobile app will only fetch relevant updates
 *
 * User: hitender
 * Date: 8/9/14 3:58 PM
 */
public interface RecentActivityManager extends RepositoryManager<RecentActivityEntity> {

    List<RecentActivityEntity> findAll(String rid, Date since);
}
