package com.receiptofi.repository;

import org.bson.types.ObjectId;

import java.util.List;

import static com.receiptofi.repository.util.AppendAdditionalFields.*;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.WriteResult;

import com.receiptofi.domain.InviteEntity;
import com.receiptofi.domain.UserProfileEntity;

/**
 * User: hitender
 * Date: 6/9/13
 * Time: 2:15 PM
 */
@Repository
@Transactional(readOnly = true)
public final class InviteManagerImpl implements InviteManager {
    private final static Logger log = Logger.getLogger(InviteManagerImpl.class);

    @Autowired private MongoTemplate mongoTemplate;

    @Override
    public InviteEntity findByAuthenticationKey(String auth) {
        Criteria criteria = Criteria.where("AUTH").is(auth);
        Query query = Query.query(criteria).addCriteria(isActive()).addCriteria(isNotDeleted());
        return mongoTemplate.findOne(query, InviteEntity.class, TABLE);
    }

    @Override
    public void invalidateAllEntries(InviteEntity object) {
        Criteria criteria = Criteria.where("USER_PROFILE_INVITED.$id").is(new ObjectId(object.getInvited().getId()));
        WriteResult writeResult = mongoTemplate.updateMulti(Query.query(criteria), update(Update.update("ACTIVE", false)), InviteEntity.class);
        log.info(writeResult.toString());
    }

    @Override
    public List<InviteEntity> getAllObjects() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void save(InviteEntity object) throws Exception {
        if(object.getId() != null) {
            object.setUpdated();
        }
        object.increaseInvitationCount();
        mongoTemplate.save(object, TABLE);
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
    public void deleteHard(InviteEntity object) {
        mongoTemplate.remove(object);
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

    @Override
    public InviteEntity reInviteActiveInvite(String emailId, UserProfileEntity invitedBy) {
        Criteria criteria1 = Criteria.where("EMAIL").is(emailId);
        Criteria criteria2 = Criteria.where("USER_PROFILE_INVITED_BY.$id").is(new ObjectId(invitedBy.getId()));
        Query query = Query.query(criteria1).addCriteria(criteria2).addCriteria(isActive()).addCriteria(isNotDeleted());
        return mongoTemplate.findOne(query, InviteEntity.class, TABLE);
    }

    @Override
    public InviteEntity find(String emailId) {
        Criteria criteria1 = Criteria.where("EMAIL").is(emailId);
        Query query = Query.query(criteria1).addCriteria(isActive()).addCriteria(isNotDeleted());
        return mongoTemplate.findOne(query, InviteEntity.class, TABLE);
    }
}
