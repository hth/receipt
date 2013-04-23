package com.tholix.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.WriteResult;

import com.tholix.domain.BizNameEntity;

/**
 * User: hitender
 * Date: 4/22/13
 * Time: 11:09 PM
 */
@Repository
@Transactional(readOnly = true)
public class BizNameManagerImpl implements BizNameManager {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<BizNameEntity> getAllObjects() {
        return mongoTemplate.findAll(BizNameEntity.class, TABLE);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void save(BizNameEntity object) throws Exception {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        mongoTemplate.save(object);
    }

    @Override
    public BizNameEntity findOne(String id) {
        return mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), BizNameEntity.class, TABLE);
    }

    @Override
    public BizNameEntity findOne(String columnName, String value) {
        Criteria criteria = Criteria.where(columnName).is(value);
        return mongoTemplate.findOne(Query.query(criteria), BizNameEntity.class, TABLE);
    }

    @Override
    public WriteResult updateObject(String id, String name) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(BizNameEntity object) {
        mongoTemplate.remove(object);
    }

    @Override
    public void createCollection() {
        if (!mongoTemplate.collectionExists(TABLE)) {
            mongoTemplate.createCollection(TABLE);
        }
    }

    @Override
    public void dropCollection() {
        if (mongoTemplate.collectionExists(TABLE)) {
            mongoTemplate.dropCollection(TABLE);
        }
    }

    @Override
    public BizNameEntity noName() {
        return mongoTemplate.findOne(Query.query(Criteria.where("name").is("")), BizNameEntity.class, TABLE);
    }

    @Override
    public List<BizNameEntity> findAllBiz(String bizName) {
        return mongoTemplate.find(Query.query(Criteria.where("name").regex(bizName, "i")), BizNameEntity.class, TABLE);
    }

    public List<String> findAllBizStr(String bizName) {
        List<String> list = new ArrayList<>();
        for (BizNameEntity bizNameEntity : findAllBiz(bizName)) {
            list.add(bizNameEntity.getName());
        }
        return list;
    }
}
