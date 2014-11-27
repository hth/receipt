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
import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 4/6/13
 * Time: 7:28 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public final class MessageDocumentManagerImpl implements MessageDocumentManager {
    private static final Logger LOG = LoggerFactory.getLogger(MessageDocumentManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            MessageDocumentEntity.class,
            Document.class,
            "collection");

    @Value ("${messageQueryLimit:10}")
    private int messageQueryLimit;

    private MongoTemplate mongoTemplate;

    @Autowired
    public MessageDocumentManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

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
        addOrder(query.limit(limit));
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
//                .append("DS", "PENDING");

        List<MessageDocumentEntity> list = findWithLimit(status);
        for (MessageDocumentEntity object : list) {
            object.setEmailId(emailId);
            object.setReceiptUserId(receiptUserId);
            object.setRecordLocked(true);
            try {
                save(object);
            } catch (Exception e) {
                LOG.error("Update failed reason={}", e.getLocalizedMessage(), e);
                object.setRecordLocked(false);
                object.setReceiptUserId(StringUtils.EMPTY);
                object.setEmailId(StringUtils.EMPTY);
                try {
                    save(object);
                } catch (Exception e1) {
                    LOG.error("Update failed {}, reason={}", object.toString(), e1.getLocalizedMessage(), e1);
                }
            }
        }

        return list;
    }

    @Override
    public List<MessageDocumentEntity> findPending(String emailId, String userProfileId, DocumentStatusEnum status) {
        Query query = query(where("LOK").is(true).and("DS").is(status).and("EM").is(emailId).and("RID").is(userProfileId));
        addOrder(query);
        return mongoTemplate.find(query, MessageDocumentEntity.class, TABLE);
    }

    @Override
    public List<MessageDocumentEntity> findAllPending() {
        Query query = query(where("LOK").is(true).and("DS").is(DocumentStatusEnum.PENDING));
        addOrder(query);
        return mongoTemplate.find(query, MessageDocumentEntity.class, TABLE);
    }

    private void addOrder(Query query) {
        List<Order> order = new ArrayList<>();
        order.add(new Order(DESC, "ULE"));
        order.add(new Order(ASC, "C"));
        query.with(new Sort(order));
    }

    @Override
    public void save(MessageDocumentEntity object) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        if (null != object.getId()) {
            object.setUpdated();
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
        Update update = update("LOK", false)
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
        mongoTemplate.remove(query(where("DID").is(documentId)), MessageDocumentEntity.class);
    }

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }

    @Override
    public void resetDocumentsToInitialState(String receiptUserId) {
        Query query = query(
                where("RID").is(receiptUserId)
                        .and("DS").is(DocumentStatusEnum.PENDING)
                        .and("LOK").is(true)
                        .and("A").is(true)
        );

        Update update = update("LOK", false)
                .unset("EM")
                .unset("RID")
                .set("U", new Date());

        mongoTemplate.updateMulti(query, update, MessageDocumentEntity.class);
    }
}
