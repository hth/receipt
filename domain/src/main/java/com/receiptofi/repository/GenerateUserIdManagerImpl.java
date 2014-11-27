package com.receiptofi.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.GenerateUserIds;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: hitender
 * Date: 4/13/14 5:19 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public final class GenerateUserIdManagerImpl implements GenerateUserIdManager {
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            GenerateUserIds.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public GenerateUserIdManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<GenerateUserIds> getAllObjects() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void save(GenerateUserIds object) {
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public GenerateUserIds findOne(String id) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public String getNextAutoGeneratedUserId() {
        mongoTemplate.updateFirst(
                query(where("_id").is(GenerateUserIds.class.getName())),
                new Update().inc("RID", 1),
                TABLE);
        GenerateUserIds generateUserIds = mongoTemplate.findById(
                GenerateUserIds.class.getName(),
                GenerateUserIds.class,
                TABLE);
        if (null == generateUserIds) {
            generateUserIds = GenerateUserIds.newInstance();
            save(generateUserIds);
        }

        return String.valueOf(generateUserIds.getAutoGeneratedReceiptUserId());
    }

    @Override
    public void deleteHard(GenerateUserIds object) {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    @Override
    public long collectionSize() {
        throw new UnsupportedOperationException("Method not implemented");
    }
}
