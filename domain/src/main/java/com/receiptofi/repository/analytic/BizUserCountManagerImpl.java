package com.receiptofi.repository.analytic;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.analytic.BizUserCountEntity;
import com.receiptofi.repository.BizNameManagerImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * User: hitender
 * Date: 6/3/16 3:32 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class BizUserCountManagerImpl implements BizUserCountManager {

    private static final Logger LOG = LoggerFactory.getLogger(BizNameManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            BizUserCountEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public BizUserCountManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public BizUserCountEntity findBy(String bizId) {
        return mongoTemplate.findOne(
                Query.query(where("bizId").is(bizId)),
                BizUserCountEntity.class,
                TABLE);
    }

    @Override
    public void save(BizUserCountEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public void deleteHard(BizUserCountEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }
}
