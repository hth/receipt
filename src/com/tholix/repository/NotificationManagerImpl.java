package com.tholix.repository;

import java.util.List;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.WriteResult;

import com.tholix.domain.NotificationEntity;

/**
 * User: hitender
 * Date: 6/30/13
 * Time: 1:38 PM
 */
@Repository
@Transactional(readOnly = true)
public class NotificationManagerImpl implements NotificationManager {
    private static final Logger log = Logger.getLogger(NotificationManagerImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<NotificationEntity> getAllObjects() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void save(NotificationEntity object) throws Exception {
        mongoTemplate.save(object);
    }

    @Override
    public NotificationEntity findOne(String id) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public WriteResult updateObject(String id, String name) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void deleteHard(NotificationEntity object) {
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
