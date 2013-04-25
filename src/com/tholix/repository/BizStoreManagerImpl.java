package com.tholix.repository;

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
import com.tholix.domain.BizStoreEntity;

/**
 * User: hitender
 * Date: 4/22/13
 * Time: 11:21 PM
 */
@Repository
@Transactional(readOnly = true)
public class BizStoreManagerImpl implements BizStoreManager {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<BizStoreEntity> getAllObjects() {
        return mongoTemplate.findAll(BizStoreEntity.class, TABLE);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void save(BizStoreEntity object) throws Exception {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        mongoTemplate.save(object);
    }

    @Override
    public BizStoreEntity findOne(String id) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public WriteResult updateObject(String id, String name) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(BizStoreEntity object) {
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

    public BizStoreEntity noStore() {
        return mongoTemplate.findOne(Query.query(Criteria.where("address").is("")), BizStoreEntity.class, TABLE);
    }

    public BizStoreEntity findOne(BizStoreEntity bizStoreEntity) {
        Criteria criteriaA = Criteria.where("address").is(bizStoreEntity.getAddress());
        Criteria criteriaB = Criteria.where("phone").is(bizStoreEntity.getPhone());

        return mongoTemplate.findOne(Query.query(criteriaA).addCriteria(criteriaB), BizStoreEntity.class, TABLE);
    }

    @Override
    public List<BizStoreEntity> findAll(String bizAddress, BizNameEntity bizNameEntity) {
        Criteria criteriaA = Criteria.where("address").regex(bizAddress, "i");
        Criteria criteriaB = Criteria.where("bizName").is(bizNameEntity);

        List<BizStoreEntity> list = mongoTemplate.find(Query.query(criteriaB).addCriteria(criteriaA), BizStoreEntity.class, TABLE);
        return list;
    }

    @Override
    public List<String> findAllAddress(String bizAddress, BizNameEntity bizNameEntity) {
        Criteria criteriaA = Criteria.where("address").regex(bizAddress, "i");
        Criteria criteriaB = Criteria.where("bizName").is(bizNameEntity);

        Query query = Query.query(criteriaB).addCriteria(criteriaA);
        query.fields().include("address");
        List<BizStoreEntity> list = mongoTemplate.find(query, BizStoreEntity.class, TABLE);

        List<String> address = new ArrayList<>();
        for(BizStoreEntity bizStoreEntity : list) {
            address.add(bizStoreEntity.getAddress());
        }

        return address;
    }
}
