package com.receiptofi.repository;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.EvalFeedbackEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: hitender
 * Date: 7/20/13
 * Time: 5:37 PM
 */
@Repository
public final class EvalFeedbackManagerImpl implements EvalFeedbackManager {
    private static final Logger LOG = LoggerFactory.getLogger(EvalFeedbackManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(EvalFeedbackEntity.class, Document.class, "collection");

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<EvalFeedbackEntity> getAllObjects() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void save(EvalFeedbackEntity object) {
        if(object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public EvalFeedbackEntity findOne(String id) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void deleteHard(EvalFeedbackEntity object) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }
}
