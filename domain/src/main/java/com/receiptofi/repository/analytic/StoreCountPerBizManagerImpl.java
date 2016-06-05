package com.receiptofi.repository.analytic;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.analytic.StoreCountPerBizEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * User: hitender
 * Date: 6/4/16 11:10 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class StoreCountPerBizManagerImpl implements StoreCountPerBizManager {
    private static final Logger LOG = LoggerFactory.getLogger(StoreCountPerBizManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            StoreCountPerBizEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public StoreCountPerBizManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public StoreCountPerBizEntity findOne(String bizId) {
        return mongoTemplate.findOne(Query.query(where("bizId").is(bizId)), StoreCountPerBizEntity.class, TABLE);
    }

    @Override
    public void save(StoreCountPerBizEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public void deleteHard(StoreCountPerBizEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }
}
