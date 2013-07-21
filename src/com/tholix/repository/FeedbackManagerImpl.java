package com.tholix.repository;

import java.util.List;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.WriteResult;

import com.tholix.domain.FeedbackEntity;

/**
 * User: hitender
 * Date: 7/20/13
 * Time: 5:37 PM
 */
public final class FeedbackManagerImpl implements FeedbackManager {
    private static final Logger log = Logger.getLogger(FeedbackManagerImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<FeedbackEntity> getAllObjects() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void save(FeedbackEntity object) throws Exception {
        mongoTemplate.save(object);
    }

    @Override
    public FeedbackEntity findOne(String id) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public WriteResult updateObject(String id, String name) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void deleteHard(FeedbackEntity object) {
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
