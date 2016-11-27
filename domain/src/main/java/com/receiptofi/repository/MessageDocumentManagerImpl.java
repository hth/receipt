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

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

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

    private List<MessageDocumentEntity> findWithLimit(DocumentStatusEnum status, int delay) {
        return findWithLimit(status, messageQueryLimit, delay);
    }

    private List<MessageDocumentEntity> findWithLimit(DocumentStatusEnum status, int limit, int delay) {
        return mongoTemplate.find(
                query(
                        where("LOK").is(false)
                                .and("DS").is(status)
                                .and("C").lte(DateTime.now().minusSeconds(delay))
                ).with(sortBy()).limit(limit),
                MessageDocumentEntity.class,
                TABLE);
    }

    @Override
    public List<MessageDocumentEntity> findUpdateWithLimit(
            String emailId,
            String rid,
            DocumentStatusEnum status,
            int delay
    ) {
        List<MessageDocumentEntity> list = findWithLimit(status, delay);
        for (MessageDocumentEntity object : list) {
            try {
                WriteResult writeResult = mongoTemplate.updateFirst(
                        query(where("id").is(object.getId())),
                        entityUpdate(
                                update("EM", emailId)
                                        .set("RID", rid)
                                        .set("LOK", true)
                        ),
                        MessageDocumentEntity.class,
                        TABLE);

                LOG.info("Update message updateOfExisting={} n={}", writeResult.isUpdateOfExisting(), writeResult.getN());
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
    public List<MessageDocumentEntity> findPending(String emailId, String rid, DocumentStatusEnum status) {
        return mongoTemplate.find(
                query(where("LOK").is(true)
                        .and("DS").is(status)
                        .and("EM").is(emailId)
                        .and("RID").is(rid)
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
    public WriteResult updateObject(String did, DocumentStatusEnum statusFind, DocumentStatusEnum statusSet) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        LOG.info("UpdateObject did={} docStatusFind={} docSetStatus={}", did, statusFind, statusSet);

        WriteResult writeResult = mongoTemplate.updateFirst(
                query(where("DID").is(did)
                        .and("DS").is(statusFind)
                        .and("LOK").is(true)),
                entityUpdate(update("DS", statusSet).set("A", false)),
                MessageDocumentEntity.class);

        LOG.info("Update message updateOfExisting={} n={}", writeResult.isUpdateOfExisting(), writeResult.getN());
        return writeResult;
    }

    @Override
    public WriteResult undoUpdateObject(String did, boolean value, DocumentStatusEnum statusFind, DocumentStatusEnum statusSet) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        return mongoTemplate.updateFirst(
                query(where("DID").is(did)
                        .and("DS").is(statusFind)
                        .and("A").is(false)
                        .and("LOK").is(true)),
                entityUpdate(update("LOK", false).set("A", true).set("DS", statusSet)),
                MessageDocumentEntity.class);
    }

    @Override
    public void deleteHard(MessageDocumentEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public int deleteAllForReceiptOCR(String did) {
        return mongoTemplate.remove(query(where("DID").is(did)), MessageDocumentEntity.class).getN();
    }

    @Override
    public void resetDocumentsToInitialState(String rid) {
        mongoTemplate.updateMulti(
                query(where("RID").is(rid)
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
    public void lockMessageWhenDuplicate(String did, String email, String rid) {
        LOG.info("Locking messages for did={} by rid={} email={}", did, rid, email);
        WriteResult writeResult = mongoTemplate.updateFirst(
                query(where("DID").is(did)),
                entityUpdate(
                        update("LOK", true)
                            .set("EM", email)
                            .set("RID", rid)
                ),
                MessageDocumentEntity.class);

        LOG.info("Update message updateOfExisting={} n={}", writeResult.isUpdateOfExisting(), writeResult.getN());
    }
}
