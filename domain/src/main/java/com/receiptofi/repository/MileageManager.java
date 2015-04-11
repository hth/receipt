package com.receiptofi.repository;

import com.receiptofi.domain.MileageEntity;

import org.joda.time.DateTime;

import java.util.List;

/**
 * User: hitender
 * Date: 12/25/13 4:16 AM
 */
public interface MileageManager extends RepositoryManager<MileageEntity> {
    MileageEntity getById(String id);

    MileageEntity findOne(String id, String receiptUserId);

    List<MileageEntity> getMileageForThisMonth(String receiptUserId, DateTime startMonth, DateTime endMonth);

    boolean updateStartDate(String mileageId, DateTime startDate, String receiptUserId);

    boolean updateEndDate(String mileageId, DateTime endDate, String receiptUserId);
}
