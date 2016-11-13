package com.receiptofi.repository;

import static com.receiptofi.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.domain.Sort.Order;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.MessageDocumentEntity;
import com.receiptofi.domain.types.DocumentStatusEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import org.junit.Assert;

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

    private int messageQueryLimit;
    private MongoTemplate mongoTemplate;

    @Autowired
    public MessageDocumentManagerImpl(
            @Value ("${messageQueryLimit:10}")
            int messageQueryLimit,

            MongoTemplate mongoTemplate) {
        this.messageQueryLimit = messageQueryLimit;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<MessageDocumentEntity> findWithLimit(DocumentStatusEnum status) {
        return findWithLimit(status, messageQueryLimit);
    }

    @Override
    public List<MessageDocumentEntity> findWithLimit(DocumentStatusEnum status, int limit) {
        return mongoTemplate.find(
                query(where("LOK").is(false).and("DS").is(status)).with(sortBy()).limit(limit),
                MessageDocumentEntity.class,
                TABLE);
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
            try {
                mongoTemplate.updateFirst(
                        query(where("id").is(object.getId())),
                        entityUpdate(update("EM", emailId).set("RID", receiptUserId).set("LOK", true)),
                        MessageDocumentEntity.class,
                        TABLE);
            } catch (Exception e) {
                LOG.error("Update failed reason={}", e.getLocalizedMessage(), e);
                object.setRecordLocked(false);
                object.setReceiptUserId("");
                object.setEmailId("");
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
        return mongoTemplate.find(
                query(where("LOK").is(true)
                        .and("DS").is(status)
                        .and("EM").is(emailId)
                        .and("RID").is(userProfileId)
                ).with(sortBy()),
                MessageDocumentEntity.class,
                TABLE);
    }

    @Override
    public List<MessageDocumentEntity> findAllPending(Date since) {
        return mongoTemplate.find(
                query(where("DS").is(DocumentStatusEnum.PENDING).and("C").lte(since)).with(sortBy()),
                MessageDocumentEntity.class,
                TABLE);
    }

    private Sort sortBy() {
        return new Sort(new Order(DESC, "UL"), new Order(ASC, "C"));
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
    public WriteResult updateObject(String documentId, DocumentStatusEnum statusFind, DocumentStatusEnum statusSet) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        return mongoTemplate.updateFirst(
                query(where("LOK").is(true).and("DS").is(statusFind).and("DID").is(documentId)),
                entityUpdate(update("DS", statusSet).set("A", false)),
                MessageDocumentEntity.class);
    }

    @Override
    public WriteResult undoUpdateObject(String documentId, boolean value, DocumentStatusEnum statusFind, DocumentStatusEnum statusSet) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        return mongoTemplate.updateFirst(
                query(where("LOK").is(true)
                        .and("DS").is(statusFind)
                        .and("A").is(false)
                        .and("DID").is(documentId)),
                entityUpdate(update("LOK", false).set("A", true).set("DS", statusSet)),
                MessageDocumentEntity.class);
    }

    @Override
    public void deleteHard(MessageDocumentEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public int deleteAllForReceiptOCR(String documentId) {
        return mongoTemplate.remove(query(where("DID").is(documentId)), MessageDocumentEntity.class).getN();
    }

    @Override
    public void resetDocumentsToInitialState(String receiptUserId) {
        mongoTemplate.updateMulti(
                query(where("RID").is(receiptUserId)
                        .orOperator(
                                where("DS").is(DocumentStatusEnum.PENDING),
                                where("DS").is(DocumentStatusEnum.REPROCESS)
                        )
                        .and("LOK").is(true)
                        .and("A").is(true)),
                entityUpdate(
                        update("LOK", false)
                                .unset("EM")
                                .unset("RID")),
                MessageDocumentEntity.class);
    }

    @Override
    public void markMessageForReceiptAsDuplicate(String did, String emailId, String rid, DocumentStatusEnum documentStatus) {
        LOG.info("Marking message as {} for did={}", documentStatus, did);
        Assert.assertEquals("Can only set to reject", DocumentStatusEnum.REJECT, documentStatus);

        WriteConcern writeConcern = mongoTemplate.getDb().getWriteConcern();
        int count = mongoTemplate.getDb().getMongo().getAllAddress().size();
        switch (count) {
            case 1:
                mongoTemplate.setWriteConcern(WriteConcern.W1);
                break;
            case 2:
                mongoTemplate.setWriteConcern(WriteConcern.W2);
                break;
            default:
                mongoTemplate.setWriteConcern(WriteConcern.W3);
                break;
        }
        mongoTemplate.updateFirst(
                query(where("DID").is(did)),
                update("LOK", true)
                        .set("DS", documentStatus)
                        .set("EM", emailId)
                        .set("RID", rid)
                        .set("A", false),
                MessageDocumentEntity.class);

        /** Re-set to the original write concern. */
        mongoTemplate.setWriteConcern(writeConcern);
    }
}
