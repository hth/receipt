package com.receiptofi.repository;

import java.util.List;

import static com.receiptofi.repository.util.AppendAdditionalFields.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.WriteResult;

import com.receiptofi.domain.ForgotRecoverEntity;

/**
 * User: hitender
 * Date: 6/4/13
 * Time: 12:10 AM
 */
@Repository
@Transactional(readOnly = true)
public final class ForgotRecoverManagerImpl implements ForgotRecoverManager {

    @Autowired private MongoTemplate mongoTemplate;

    @Override
    public List<ForgotRecoverEntity> getAllObjects() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void save(ForgotRecoverEntity object) throws Exception {
        if(object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void invalidateAllEntries(ForgotRecoverEntity object) {
        Criteria criteria = Criteria.where("USER_PROFILE_ID").is(object.getUserProfileId());
        mongoTemplate.updateMulti(Query.query(criteria), update(Update.update("A", false)), ForgotRecoverEntity.class);
    }

    @Override
    public ForgotRecoverEntity findOne(String id) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public ForgotRecoverEntity findByAuthenticationKey(String key) {
        Criteria criteria = Criteria.where("AUTH").is(key);
        Query query = Query.query(criteria).addCriteria(isActive()).addCriteria(isNotDeleted());
        return mongoTemplate.findOne(query, ForgotRecoverEntity.class, TABLE);
    }

    @Override
    public WriteResult updateObject(String id, String name) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void deleteHard(ForgotRecoverEntity object) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void createCollection() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void dropCollection() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }
}
