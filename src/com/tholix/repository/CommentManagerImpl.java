package com.tholix.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.mongodb.WriteResult;

import com.tholix.domain.CommentEntity;

/**
 * User: hitender
 * Date: 6/11/13
 * Time: 7:13 PM
 */
@Repository
public final class CommentManagerImpl implements CommentManager {
    @Autowired private MongoTemplate mongoTemplate;

    @Override
    public List<CommentEntity> getAllObjects() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    /**
     * Note: comment should not be marked updated with new time as its already updated by Ajax
     *
     * @param object
     * @throws Exception
     */
    @Override
    public void save(CommentEntity object) throws Exception {
        //Note: comment should not be marked updated with new time as its already updated by Ajax
//        if(object.getId() != null) {
//            object.setUpdated();
//        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public CommentEntity findOne(String id) {
        return mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), CommentEntity.class);
    }

    @Override
    public WriteResult updateObject(String id, String name) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void deleteHard(CommentEntity object) {
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
}
