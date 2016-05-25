package com.receiptofi.repository;

import static com.receiptofi.repository.util.AppendAdditionalFields.isActive;
import static com.receiptofi.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.BusinessUserEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * User: hitender
 * Date: 5/16/16 3:46 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class BusinessUserManagerImpl implements BusinessUserManager {
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            BusinessUserEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public BusinessUserManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public BusinessUserEntity findByRid(String rid) {
        return mongoTemplate.findOne(
                query(where("RID").is(rid)),
                BusinessUserEntity.class,
                TABLE);
    }

    @Override
    public BusinessUserEntity findBusinessUser(String rid) {
        return mongoTemplate.findOne(
                query(where("RID").is(rid)
                        .andOperator(
                            isActive(),
                            isNotDeleted()
                        )
                ),
                BusinessUserEntity.class,
                TABLE);
    }

    @Override
    public void save(BusinessUserEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public void deleteHard(BusinessUserEntity object) {
        /** Do not implement this method. No hard delete for business user. */
        throw new UnsupportedOperationException("Method not implemented");
    }
}
