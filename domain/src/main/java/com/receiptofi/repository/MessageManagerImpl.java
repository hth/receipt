package com.receiptofi.repository;

import static com.receiptofi.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.domain.Sort.Order;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.mongodb.WriteResult;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.MessageDocumentEntity;
import com.receiptofi.domain.types.DocumentStatusEnum;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 4/6/13
 * Time: 7:28 PM
 */
@Repository
public final class MessageManagerImpl implements MessageManager {
    private static final Logger LOG = LoggerFactory.getLogger(MessageManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            MessageDocumentEntity.class,
            Document.class,
            "collection");

    private static final Sort SORT_BY_USER_LEVEL_AND_CREATED = new Sort(
            new ArrayList<Order>() {{
                add(new Order(DESC, "ULE"));
                add(new Order(ASC, "C"));
            }}
    );

    @Value ("${messageQueryLimit:10}")
    private int messageQueryLimit;

    @Autowired private MongoTemplate mongoTemplate;

    @Override
    public List<MessageDocumentEntity> getAllObjects() {
        return mongoTemplate.findAll(MessageDocumentEntity.class, TABLE);
    }

    @Override
    public List<MessageDocumentEntity> findWithLimit(DocumentStatusEnum status) {
        return findWithLimit(status, messageQueryLimit);
    }

    @Override
    public List<MessageDocumentEntity> findWithLimit(DocumentStatusEnum status, int limit) {
        Query query = query(where("LOK").is(false).and("DS").is(status));
        query.with(SORT_BY_USER_LEVEL_AND_CREATED).limit(limit);
        return mongoTemplate.find(query, MessageDocumentEntity.class, TABLE);
    }

    @Override
    public List<MessageDocumentEntity> findUpdateWithLimit(String emailId, String receiptUserId, DocumentStatusEnum status) {
        return findUpdateWithLimit(emailId, receiptUserId, status, messageQueryLimit);
    }

    @Override
    public List<MessageDocumentEntity> findUpdateWithLimit(String emailId, String receiptUserId, DocumentStatusEnum status, int limit) {
//        String updateQuery = "{ " +
//                "set : " +
//                    "{" +
//                    "'emailId' : '" + emailId + "', " +
//                    "'profileId' : '" + profileId + "', " +
//                    "'recordLocked' : " + true +
//                    "} " +
//                "}";
//
//        String sortQuery  = "{ sort : { 'level' : " + -1 + ", 'created' : " + 1 + "} }";
//        String limitQuery = "{ limit : " + messageQueryLimit + "}";

//        BasicDBObject basicDBObject = new BasicDBObject()
//                .append("recordLocked", false)
//                .append("DS", "OCR_PROCESSED");

        List<MessageDocumentEntity> list = findWithLimit(status);
        for (MessageDocumentEntity object : list) {
            object.setEmailId(emailId);
            object.setReceiptUserId(receiptUserId);
            object.setRecordLocked(true);
            try {
                save(object);
            } catch (Exception e) {
                object.setRecordLocked(false);
                object.setReceiptUserId(StringUtils.EMPTY);
                object.setEmailId(StringUtils.EMPTY);
                try {
                    save(object);
                } catch (Exception e1) {
                    LOG.error("Update failed: " + object.toString());
                }
            }
        }

        return list;
    }

    @Override
    public List<MessageDocumentEntity> findPending(String emailId, String userProfileId, DocumentStatusEnum status) {
        Query query = query(where("LOK").is(true).and("DS").is(status).and("EM").is(emailId).and("RID").is(userProfileId));
        query.with(SORT_BY_USER_LEVEL_AND_CREATED);
        return mongoTemplate.find(query, MessageDocumentEntity.class, TABLE);
    }

    @Override
    public List<MessageDocumentEntity> findAllPending() {
        Query query = query(where("LOK").is(true).and("DS").is(DocumentStatusEnum.OCR_PROCESSED));
        query.with(SORT_BY_USER_LEVEL_AND_CREATED);
        return mongoTemplate.find(query, MessageDocumentEntity.class, TABLE);
    }

    @Override
    public void save(MessageDocumentEntity object) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        if (object.getId() != null) {
            object.setUpdated(); //TODO why force the update date. Should it not be handled by the system just like versioning.
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public MessageDocumentEntity findOne(String id) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public WriteResult updateObject(String documentId, DocumentStatusEnum statusFind, DocumentStatusEnum statusSet) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        Query query = query(where("LOK").is(true).and("DS").is(statusFind).and("DID").is(documentId));
        Update update = update("DS", statusSet).set("A", false);
        return mongoTemplate.updateFirst(query, entityUpdate(update), MessageDocumentEntity.class);
    }

    @Override
    public WriteResult undoUpdateObject(String documentId, boolean value, DocumentStatusEnum statusFind, DocumentStatusEnum statusSet) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        Query query = query(where("LOK").is(true).and("DS").is(statusFind).and("A").is(false).and("DID").is(documentId));
        Update update = update("recordLocked", false)
                .set("A", true)
                .set("DS", statusSet);
        return mongoTemplate.updateFirst(query, entityUpdate(update), MessageDocumentEntity.class);
    }

    @Override
    public void deleteHard(MessageDocumentEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public void deleteAllForReceiptOCR(String documentId) {
        Query query = query(where("DID").is(documentId));
        mongoTemplate.remove(query, MessageDocumentEntity.class);
    }

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }
}
