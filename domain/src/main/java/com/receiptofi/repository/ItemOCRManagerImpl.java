/**
 *
 */
package com.receiptofi.repository;

import static com.receiptofi.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.mongodb.WriteResult;

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
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Autowired private MongoTemplate mongoTemplate;

    @Override
    public List<ItemEntityOCR> getAllObjects() {
        return mongoTemplate.findAll(ItemEntityOCR.class, TABLE);
    }

    @Override
    public void save(ItemEntityOCR object) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
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
        for (ItemEntityOCR object : objects) {
            save(object);
        }
    }

    @Override
    public ItemEntityOCR findOne(String id) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public List<ItemEntityOCR> getWhereReceipt(DocumentEntity receipt) {
        Query query = query(where("DOCUMENT.$id").is(new ObjectId(receipt.getId())));
        Sort sort = new Sort(Direction.ASC, "SEQ");
        return mongoTemplate.find(query.with(sort), ItemEntityOCR.class, TABLE);
    }

    @Override
    public void deleteHard(ItemEntityOCR object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public void deleteWhereReceipt(DocumentEntity receipt) {
        Query query = query(where("DOCUMENT.$id").is(new ObjectId(receipt.getId())));
        mongoTemplate.remove(query, ItemEntityOCR.class);
    }

    @Override
    public WriteResult updateObject(ItemEntityOCR object) {
        Query query = query(where("id").is(object.getId()));
        Update update = update("IN", object.getName());
        return mongoTemplate.updateFirst(query, entityUpdate(update), TABLE);
    }

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }
}
