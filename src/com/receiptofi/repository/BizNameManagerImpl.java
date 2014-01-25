package com.receiptofi.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.WriteResult;

import com.receiptofi.domain.BizNameEntity;

/**
 * User: hitender
 * Date: 4/22/13
 * Time: 11:09 PM
 */
@Repository
@Transactional(readOnly = true)
public final class BizNameManagerImpl implements BizNameManager {

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
        if(object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public BizNameEntity findOne(String id) {
        return mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), BizNameEntity.class, TABLE);
    }

    @Override
    public BizNameEntity findOneByName(String name) {
        Criteria criteria = Criteria.where("NAME").is(name);
        return mongoTemplate.findOne(Query.query(criteria), BizNameEntity.class, TABLE);
    }

    @Override
    public WriteResult updateObject(String id, String name) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteHard(BizNameEntity object) {
        mongoTemplate.remove(object);
    }

    @Override
    public BizNameEntity noName() {
        return mongoTemplate.findOne(Query.query(Criteria.where("NAME").is("")), BizNameEntity.class, TABLE);
    }

    @Override
    public List<BizNameEntity> findAllBiz(String bizName) {
        return mongoTemplate.find(Query.query(Criteria.where("NAME").regex("^" + bizName, "i")), BizNameEntity.class, TABLE);
    }

    public Set<String> findAllDistinctBizStr(String bizName) {
        Set<String> set = new HashSet<>();
        for (BizNameEntity bizNameEntity : findAllBiz(bizName)) {
            set.add(bizNameEntity.getName());
        }
        return set;
    }

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }
}
