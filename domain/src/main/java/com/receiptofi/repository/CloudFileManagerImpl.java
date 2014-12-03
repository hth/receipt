package com.receiptofi.repository;

import static com.receiptofi.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.CloudFileEntity;
import com.receiptofi.domain.ReceiptEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * User: hitender
 * Date: 12/2/14 6:37 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class CloudFileManagerImpl implements CloudFileManager {
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            CloudFileEntity.class,
            Document.class,
            "collection");

    @Autowired private MongoTemplate mongoTemplate;

    @Override
    public void save(CloudFileEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public CloudFileEntity findOne(String id) {
        return mongoTemplate.findOne(query(where("id").is(id)), CloudFileEntity.class);
    }

    @Override
    public void deleteSoft(CloudFileEntity object) {
        Query query = query(where("id").is(object.getId()));
        Update update = Update.update("D", true);
        mongoTemplate.updateFirst(query, entityUpdate(update), CloudFileEntity.class);
    }

    @Override
    public void deleteHard(CloudFileEntity object) {
        mongoTemplate.remove(object);
    }
}
