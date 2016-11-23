package com.receiptofi.repository;

import static com.receiptofi.repository.util.AppendAdditionalFields.entityUpdate;
import static com.receiptofi.repository.util.AppendAdditionalFields.isActive;
import static com.receiptofi.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.ForgotRecoverEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

/**
 * User: hitender
 * Date: 6/4/13
 * Time: 12:10 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public final class ForgotRecoverManagerImpl implements ForgotRecoverManager {
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            ForgotRecoverEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public ForgotRecoverManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(ForgotRecoverEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void invalidateAllEntries(String receiptUserId) {
        mongoTemplate.updateMulti(
                query(where("RID").is(receiptUserId)),
                entityUpdate(update("A", false)),
                ForgotRecoverEntity.class);
    }

    @Override
    public ForgotRecoverEntity findByAuthenticationKey(String key) {
        return mongoTemplate.findOne(
                query(where("AUTH").is(key)
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )
                ),
                ForgotRecoverEntity.class,
                TABLE);
    }

    @Override
    public void deleteHard(ForgotRecoverEntity object) {
        mongoTemplate.remove(object);
    }
}
