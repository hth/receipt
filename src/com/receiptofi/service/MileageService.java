package com.receiptofi.service;

import com.receiptofi.domain.MileageEntity;
import com.receiptofi.repository.MileageManager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.joda.time.DateTime;

/**
 * User: hitender
 * Date: 12/25/13 4:16 AM
 */
@Service
public final class MileageService {

    @Autowired private MileageManager mileageManager;

    public void save(MileageEntity mileageEntity) throws Exception {
        mileageManager.save(mileageEntity);
    }

    public List<MileageEntity> getMileageForThisMonth(String profileId, DateTime monthYear) {
        return mileageManager.getMileageForThisMonth(profileId, monthYear);
    }
}
