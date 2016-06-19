package com.receiptofi.repository.analytic;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.*;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.analytic.BizDimensionEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * User: hitender
 * Date: 6/8/16 4:20 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class BizDimensionManagerImpl implements BizDimensionManager {
    private static final Logger LOG = LoggerFactory.getLogger(BizDimensionManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            BizDimensionEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public BizDimensionManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public BizDimensionEntity findBy(String bizId) {
        return mongoTemplate.findOne(
                query(where("bizId").is(bizId)),
                BizDimensionEntity.class,
                TABLE);
    }

    @Override
    public void save(BizDimensionEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public void deleteHard(BizDimensionEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }
}
