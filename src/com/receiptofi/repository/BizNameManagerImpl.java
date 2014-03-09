package com.receiptofi.repository;

import com.receiptofi.domain.BizNameEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.mongodb.WriteResult;

/**
 * User: hitender
 * Date: 4/22/13
 * Time: 11:09 PM
 */
@Repository
public final class BizNameManagerImpl implements BizNameManager {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<BizNameEntity> getAllObjects() {
        return mongoTemplate.findAll(BizNameEntity.class, TABLE);
    }

    @Override
    public void save(BizNameEntity object) throws Exception {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        if(object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public BizNameEntity findOne(String id) {
        return mongoTemplate.findOne(query(where("id").is(id)), BizNameEntity.class, TABLE);
    }

    @Override
    public BizNameEntity findOneByName(String businessName) {
        Criteria criteria = where("N").is(businessName);
        return mongoTemplate.findOne(query(criteria), BizNameEntity.class, TABLE);
    }

    @Override
    public WriteResult updateObject(String id, String name) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void deleteHard(BizNameEntity object) {
        mongoTemplate.remove(object);
    }

    @Override
    public BizNameEntity noName() {
        return mongoTemplate.findOne(query(where("N").is("")), BizNameEntity.class, TABLE);
    }

    @Override
    public List<BizNameEntity> findAllBizWithMatchingName(String businessName) {
        return mongoTemplate.find(query(where("N").regex("^" + businessName, "i")), BizNameEntity.class, TABLE);
    }

    public Set<String> findAllDistinctBizStr(String businessName) {
        Set<String> set = new HashSet<>();
        for (BizNameEntity bizNameEntity : findAllBizWithMatchingName(businessName)) {
            set.add(bizNameEntity.getBusinessName());
        }
        return set;
    }

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }
}
