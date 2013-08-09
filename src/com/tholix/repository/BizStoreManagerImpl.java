package com.tholix.repository;

import org.bson.types.ObjectId;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
public final class BizStoreManagerImpl implements BizStoreManager {

    @Autowired private MongoTemplate mongoTemplate;

    @Override
    public List<BizStoreEntity> getAllObjects() {
        return mongoTemplate.findAll(BizStoreEntity.class, TABLE);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void save(BizStoreEntity object) throws Exception {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        if(object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
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
    public void deleteHard(BizStoreEntity object) {
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
        return mongoTemplate.findOne(Query.query(Criteria.where("ADDRESS").is("")), BizStoreEntity.class, TABLE);
    }

    public BizStoreEntity findOne(BizStoreEntity bizStoreEntity) {
        Query query = Query.query(Criteria.where("ADDRESS").is(bizStoreEntity.getAddress()));

        if(StringUtils.isNotEmpty(bizStoreEntity.getPhone())) {
            query.addCriteria(Criteria.where("PHONE").is(bizStoreEntity.getPhone()));
        }

        return mongoTemplate.findOne(query, BizStoreEntity.class, TABLE);
    }

    @Override
    public List<BizStoreEntity> findAllWithAnyAddressAnyPhone(String bizAddress, String bizPhone, BizNameEntity bizNameEntity) {
        Criteria criteriaA = new Criteria();
        if(StringUtils.isNotEmpty(bizAddress)) {
            criteriaA.andOperator(Criteria.where("ADDRESS").regex(bizAddress, "i"));
        }
        if(StringUtils.isNotEmpty(bizPhone)) {
            criteriaA.andOperator(Criteria.where("PHONE").regex(bizPhone, "i"));
        }

        if(bizNameEntity != null && StringUtils.isNotEmpty(bizNameEntity.getId())) {
            Criteria criteriaB = Criteria.where("BIZ_NAME.$id").is(new ObjectId(bizNameEntity.getId()));
            return mongoTemplate.find(Query.query(criteriaB).addCriteria(criteriaA).limit(30), BizStoreEntity.class, TABLE);
        } else {
            return mongoTemplate.find(Query.query(criteriaA).limit(30), BizStoreEntity.class, TABLE);
        }
    }

    @Override
    public List<BizStoreEntity> findAllWithStartingAddressStartingPhone(String bizAddress, String bizPhone, BizNameEntity bizNameEntity) {
        Criteria criteriaA = new Criteria();
        if(StringUtils.isNotEmpty(bizAddress)) {
            criteriaA.andOperator(Criteria.where("ADDRESS").regex("^" + bizAddress, "i"));
        }
        if(StringUtils.isNotEmpty(bizPhone)) {
            criteriaA.andOperator(Criteria.where("PHONE").regex("^" + bizPhone, "i"));
        }

        if(bizNameEntity != null && StringUtils.isNotEmpty(bizNameEntity.getId())) {
            Criteria criteriaB = Criteria.where("BIZ_NAME.$id").is(new ObjectId(bizNameEntity.getId()));
            return mongoTemplate.find(Query.query(criteriaB).addCriteria(criteriaA).limit(30), BizStoreEntity.class, TABLE);
        } else {
            return mongoTemplate.find(Query.query(criteriaA).limit(30), BizStoreEntity.class, TABLE);
        }
    }

    @Override
    public List<BizStoreEntity> getAllWithJustSpecificField(String bizAddress, BizNameEntity bizNameEntity, String fieldName) {
        Criteria criteriaA = Criteria.where("ADDRESS").regex("^" + bizAddress, "i");
        Criteria criteriaB = Criteria.where("BIZ_NAME.$id").is(new ObjectId(bizNameEntity.getId()));

        Query query = Query.query(criteriaB).addCriteria(criteriaA);
        query.fields().include(fieldName);
        return mongoTemplate.find(query, BizStoreEntity.class, TABLE);
    }

    @Override
    public List<BizStoreEntity> getAllWithJustSpecificField(String bizPhone, String bizAddress, BizNameEntity bizNameEntity, String fieldName) {
        Criteria criteriaA = Criteria.where("PHONE").regex("^" + bizPhone, "i");
        Criteria criteriaB = Criteria.where("ADDRESS").is(bizAddress);
        Criteria criteriaC = Criteria.where("BIZ_NAME.$id").is(new ObjectId(bizNameEntity.getId()));

        Query query = Query.query(criteriaC).addCriteria(criteriaB).addCriteria(criteriaA);
        query.fields().include(fieldName);
        return mongoTemplate.find(query, BizStoreEntity.class, TABLE);
    }

    @Override
    public List<BizStoreEntity> findAllAddress(BizNameEntity bizNameEntity, int limit) {
        Sort sort = new Sort(Sort.Direction.DESC, "CREATE");
        return mongoTemplate.find(Query.query(Criteria.where("BIZ_NAME.$id").is(new ObjectId(bizNameEntity.getId()))).with(sort).limit(limit), BizStoreEntity.class, TABLE);
    }

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }
}
