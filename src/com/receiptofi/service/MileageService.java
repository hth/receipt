package com.receiptofi.service;

import com.receiptofi.domain.MileageEntity;
import com.receiptofi.repository.MileageManager;

import java.util.LinkedList;
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

    public MileageEntity merge(String id1, String id2, String userProfileId) {
        MileageEntity m1 = mileageManager.findOne(id1, userProfileId);
        MileageEntity m2 = mileageManager.findOne(id2, userProfileId);

        try {
            if(m1 != null && m2 != null && !m1.isComplete() && !m2.isComplete()) {
                if(Integer.compare(m1.getStart(), m2.getStart()) == 1) {
                    m2.mergeEndingMileage(m1);
                    mileageManager.save(m2);
                    mileageManager.deleteHard(m1);
                    return m2;
                } else if(Integer.compare(m1.getStart(), m2.getStart()) == -1) {
                    m1.mergeEndingMileage(m2);
                    mileageManager.save(m1);
                    mileageManager.deleteHard(m2);
                    return m1;
                } else if(Integer.compare(m1.getStart(), m2.getStart()) == 0) {
                    //There should not be a duplicate data; it should have been rejected
                    throw new RuntimeException("Merge failed as both the start point are equal");
                }
            }
        } catch(Exception exception) {
            throw new RuntimeException("Merge failed to save");
        }
        throw new RuntimeException("Merge failed as one or both could not be merged");
    }

    public List<MileageEntity> split(String id, String userProfileId) {
        MileageEntity m1 = mileageManager.findOne(id, userProfileId);
        try {
            if(m1 != null && m1.isComplete()) {
                MileageEntity m2 = m1.splitMileage();
                mileageManager.save(m1);
                mileageManager.save(m2);

                List<MileageEntity> list = new LinkedList<>();
                list.add(m2);
                list.add(m1);
                return list;
            }
        } catch(Exception exception) {
            throw new RuntimeException("During split failed to save");
        }
        throw new RuntimeException("Could not process split");
    }
}
