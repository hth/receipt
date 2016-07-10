package com.receiptofi.repository;

import static com.receiptofi.repository.util.AppendAdditionalFields.isActive;
import static com.receiptofi.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.EvalFeedbackEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: hitender
 * Date: 7/20/13
 * Time: 5:37 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public final class EvalFeedbackManagerImpl implements EvalFeedbackManager {
    private static final Logger LOG = LoggerFactory.getLogger(EvalFeedbackManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            EvalFeedbackEntity.class,
            Document.class,
            "collection");

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(EvalFeedbackEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(EvalFeedbackEntity object) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public List<EvalFeedbackEntity> latestFeedback(int limit) {
        return mongoTemplate.find(
                query(new Criteria()
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )
                ).with(new Sort(DESC, "U")).limit(limit),
                EvalFeedbackEntity.class,
                TABLE
        );
    }

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }
}
