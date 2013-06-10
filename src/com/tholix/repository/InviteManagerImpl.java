package com.tholix.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.WriteResult;

import com.tholix.domain.InviteEntity;

/**
 * User: hitender
 * Date: 6/9/13
 * Time: 2:15 PM
 */
@Repository
@Transactional(readOnly = true)
public class InviteManagerImpl implements InviteManager {

    @Autowired private MongoTemplate mongoTemplate;

    @Override
    public InviteEntity findByAuthenticationKey(String key) {
        Criteria criteria = Criteria.where("authenticationKey").is(key).andOperator(Criteria.where("active").is(true));
        return mongoTemplate.findOne(Query.query(criteria), InviteEntity.class, TABLE);
    }

    @Override
    public void invalidateAllEntries(InviteEntity object) {
        Criteria criteria = Criteria.where("newInvitedUser").is(object.getNewInvitedUser());
        mongoTemplate.updateMulti(Query.query(criteria), Update.update("active", false), InviteEntity.class);
    }

    @Override
    public List<InviteEntity> getAllObjects() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void save(InviteEntity object) throws Exception {
        mongoTemplate.save(object);
    }

    @Override
    public InviteEntity findOne(String id) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public WriteResult updateObject(String id, String name) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void delete(InviteEntity object) {
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
