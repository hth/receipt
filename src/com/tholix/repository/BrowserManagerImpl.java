package com.tholix.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.mongodb.WriteResult;

import com.tholix.domain.BrowserEntity;

/**
 * User: hitender
 * Date: 5/26/13
 * Time: 4:08 PM
 */
@Repository
public class BrowserManagerImpl implements BrowserManager {

    @Autowired private MongoTemplate mongoTemplate;

    @Override
    public List<BrowserEntity> getAllObjects() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void save(BrowserEntity object) throws Exception {
        mongoTemplate.save(object);
    }

    @Override
    public BrowserEntity findOne(String cookieId) {
        return mongoTemplate.findOne(Query.query(Criteria.where("cookieId").is(cookieId)), BrowserEntity.class);
    }

    @Override
    public WriteResult updateObject(String id, String name) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void delete(BrowserEntity object) {
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
}
