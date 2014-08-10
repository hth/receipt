package com.receiptofi.repository;

import java.util.Date;
import java.util.List;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.RecentActivityEntity;
import com.receiptofi.domain.annotation.Mobile;
import com.receiptofi.domain.types.RecentActivityEnum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * User: hitender
 * Date: 8/9/14 4:05 PM
 */
@Mobile
@Repository
public final class RecentActivityManagerImpl implements RecentActivityManager {
    private static final String TABLE = BaseEntity.getClassAnnotationValue(RecentActivityEntity.class, Document.class, "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public RecentActivityManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<RecentActivityEntity> getAllObjects() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void save(RecentActivityEntity object) {
        RecentActivityEntity recentActivity = findOne(object.getUserProfileId(), object.getRecentActivity());
        if (recentActivity == null) {
            mongoTemplate.save(object);
        } else if (recentActivity.getEarliestUpdate().after(object.getEarliestUpdate())) {
            recentActivity.setEarliestUpdate(object.getEarliestUpdate());
            recentActivity.setUpdated();
            mongoTemplate.save(recentActivity);
        }
    }

    private RecentActivityEntity findOne(String rid, RecentActivityEnum recentActivityEnum) {
        return mongoTemplate.findOne(query(where("RID").is(rid).and("RA").is(recentActivityEnum)), RecentActivityEntity.class);
    }

    @Override
    public List<RecentActivityEntity> findAll(String rid, Date earliestUpdate) {
        return mongoTemplate.find(query(where("RID").is(rid).and("EL").gte(earliestUpdate)), RecentActivityEntity.class, TABLE);
    }

    @Override
    public RecentActivityEntity findOne(String id) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void deleteHard(RecentActivityEntity object) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public long collectionSize() {
        throw new UnsupportedOperationException("Method not implemented");
    }
}
