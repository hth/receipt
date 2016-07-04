package com.receiptofi.repository.analytic;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.analytic.StoreDimensionEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

/**
 * User: hitender
 * Date: 7/2/16 3:39 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class StoreDimensionManagerImpl implements StoreDimensionManager {
    private static final Logger LOG = LoggerFactory.getLogger(BizDimensionManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            StoreDimensionEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public StoreDimensionManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(StoreDimensionEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public void deleteHard(StoreDimensionEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }
}
