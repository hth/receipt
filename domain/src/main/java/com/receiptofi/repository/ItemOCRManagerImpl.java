/**
 *
 */
package com.receiptofi.repository;

import com.mongodb.client.result.UpdateResult;
import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.ItemEntityOCR;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.receiptofi.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

/**
 * @author hitender
 * @since Jan 6, 2013 1:35:47 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public final class ItemOCRManagerImpl implements ItemOCRManager {
    private static final Logger LOG = LoggerFactory.getLogger(ItemOCRManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            ItemEntityOCR.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public ItemOCRManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(ItemEntityOCR object) {
        try {
            if (object.getId() != null) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } catch (DataIntegrityViolationException e) {
            LOG.error("Duplicate record entry for ItemEntityOCR={}", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void saveObjects(List<ItemEntityOCR> objects) throws Exception {
        //TODO reflection error saving the list
        //mongoTemplate.insert(objects, TABLE);
        objects.forEach(this::save);
    }

    @Override
    public List<ItemEntityOCR> getWhereReceipt(DocumentEntity receipt) {
        return mongoTemplate.find(
                query(where("DOCUMENT.$id").is(new ObjectId(receipt.getId()))).with(new Sort(Direction.ASC, "SEQ")),
                ItemEntityOCR.class,
                TABLE);
    }

    @Override
    public void deleteHard(ItemEntityOCR object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public void deleteWhere(String did) {
        mongoTemplate.remove(
                query(where("DOCUMENT.$id").is(new ObjectId(did))),
                ItemEntityOCR.class,
                TABLE);
    }

    @Override
    public UpdateResult updateObject(ItemEntityOCR object) {
        return mongoTemplate.updateFirst(
                query(where("id").is(object.getId())),
                entityUpdate(update("IN", object.getName())),
                TABLE);
    }

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }
}
