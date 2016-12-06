/**
 *
 */
package com.receiptofi.repository;

import static com.receiptofi.repository.util.AppendAdditionalFields.entityUpdate;
import static com.receiptofi.repository.util.AppendAdditionalFields.isActive;
import static com.receiptofi.repository.util.AppendAdditionalFields.isDeleted;
import static com.receiptofi.repository.util.AppendAdditionalFields.isNotActive;
import static com.receiptofi.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.mongodb.ReadPreference;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.types.DocumentStatusEnum;
import com.receiptofi.domain.value.DocumentGrouped;
import com.receiptofi.utils.DateUtil;

import org.joda.time.DateTime;
import org.joda.time.Days;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author hitender
 * @since Jan 6, 2013 1:29:44 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public final class DocumentManagerImpl implements DocumentManager {
    private static final Logger LOG = LoggerFactory.getLogger(ReceiptManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            DocumentEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public DocumentManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(DocumentEntity object) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        try {
            // Cannot use insert because insert does not perform update like save.
            // Save will always try to update or create new record.
            // mongoTemplate.insert(object, TABLE);

            if (object.getId() != null) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } catch (DataIntegrityViolationException e) {
            LOG.error("Duplicate record entry for DocumentEntity={}", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public DocumentEntity findDocumentByRid(String id, String rid) {
        return mongoTemplate.findOne(
                query(where("id").is(id).and("RID").is(rid)),
                DocumentEntity.class,
                TABLE
        );
    }

    /**
     * Under replica mode, read from primary.
     *
     * @param id
     * @return
     */
    @Override
    public DocumentEntity findActiveOne(String id) {
        Assert.hasText(id, "Id is empty");
        /*
        * Force read from primary as secondary might not have been updated. Duplicate auto reject fails as document
        * might not have been propagated to replica set.
        */
        mongoTemplate.setReadPreference(ReadPreference.primary());
        DocumentEntity document = mongoTemplate.findOne(
                query(where("id").is(id).andOperator(isActive())),
                DocumentEntity.class,
                TABLE
        );
        mongoTemplate.setReadPreference(ReadPreference.nearest());
        return document;
    }

    @Override
    public DocumentEntity findRejectedOne(String id) {
        return mongoTemplate.findOne(
                query(where("id").is(id).and("DS").is(DocumentStatusEnum.REJECT)),
                DocumentEntity.class,
                TABLE
        );
    }

    @Override
    public void deleteHard(DocumentEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public long numberOfPendingReceipts(String rid) {
        return mongoTemplate.count(
                query(where("RID").is(rid)
                        .andOperator(
                                isActive(),
                                isNotDeleted())
                ),
                TABLE
        );
    }

    @Override
    public long numberOfRejectedReceipts(String rid) {
        return mongoTemplate.count(
                query(where("RID").is(rid).and("DS").is(DocumentStatusEnum.REJECT)
                        .andOperator(
                                isNotActive(),
                                isDeleted())
                ),
                TABLE
        );
    }

    @Override
    public List<DocumentEntity> getAllPending(String rid) {
        return mongoTemplate.find(
                query(where("RID").is(rid)
                        .andOperator(
                                isActive(),
                                isNotDeleted())
                ).with(new Sort(Direction.ASC, "C")),
                DocumentEntity.class,
                TABLE
        );
    }

    @Override
    public List<DocumentEntity> getAllRejected(String rid) {
        return mongoTemplate.find(
                query(where("RID").is(rid)
                        .and("DS").is(DocumentStatusEnum.REJECT)
                        .andOperator(
                                isNotActive(),
                                isDeleted())
                ).with(new Sort(Direction.ASC, "C")),
                DocumentEntity.class,
                TABLE
        );
    }

    @Override
    public List<DocumentEntity> getAllRejected(int purgeRejectedDocumentAfterDay) {
        return mongoTemplate.find(
                query(where("DS").is(DocumentStatusEnum.REJECT)
                        .and("U").lte(DateTime.now().minusDays(purgeRejectedDocumentAfterDay))
                        .andOperator(
                                isNotActive(),
                                isDeleted()
                        )
                ),
                DocumentEntity.class,
                TABLE
        );
    }

    @Override
    public List<DocumentEntity> getDocumentsForNotification(int delay) {
        return mongoTemplate.find(
                query(where("NU").is(false)
                        .and("U").lte(DateTime.now().minusMinutes(delay)))
                        .with(new Sort(Direction.ASC, "U")),
                DocumentEntity.class,
                TABLE
        );
    }

    @Override
    public List<DocumentEntity> getAllProcessedDocuments() {
        return mongoTemplate.find(
                query(where("IU").is(false)
                        .and("DS").is(DocumentStatusEnum.PROCESSED)
                        .andOperator(
                                isNotActive(),
                                isNotDeleted()
                        )
                ).with(new Sort(Direction.ASC, "U")),
                DocumentEntity.class,
                TABLE
        );
    }

    @Override
    public long getTotalPending() {
        return mongoTemplate.count(
                query(where("DS").in(
                        DocumentStatusEnum.PENDING,
                        DocumentStatusEnum.REPROCESS
                ).andOperator(isNotDeleted())),
                DocumentEntity.class
        );
    }

    @Override
    public long getTotalProcessedToday() {
        return mongoTemplate.count(
                query(where("DS").ne(DocumentStatusEnum.PENDING)
                        .and("U").gte(DateUtil.midnight(new Date()))
                        .andOperator(isNotDeleted())),
                DocumentEntity.class
        );
    }

    @Override
    public Iterator<DocumentGrouped> getHistoricalStat(Date since) {
        GroupBy groupBy = GroupBy.key("C", "DS")
                .initialDocument("{ total: 0 }")
                .reduceFunction("function(obj, result) { " +
                        "  result.day = obj.C;" +
                        "  result.documentStatusEnum = obj.DS;" +
                        "}");

        Criteria criteria = where("C").gte(DateUtil.midnight(DateUtil.now().minusDays(
                Days.daysBetween(new DateTime(since), DateUtil.midnight(DateTime.now())).getDays())))
                .lt(DateUtil.midnight(DateUtil.now()))
                .andOperator(
                        isNotDeleted()
                );

        GroupByResults<DocumentGrouped> results = mongoTemplate.group(criteria, TABLE, groupBy, DocumentGrouped.class);
        return results.iterator();
    }

    @Override
    public void cloudUploadSuccessful(String documentId) {
        mongoTemplate.updateFirst(
                query(where("id").is(documentId)),
                entityUpdate(update("IU", true)),
                DocumentEntity.class,
                TABLE
        );
    }

    @Override
    public void markNotified(String documentId) {
        mongoTemplate.updateFirst(
                query(where("id").is(documentId)),
                entityUpdate(update("NU", true)),
                DocumentEntity.class,
                TABLE
        );
    }

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }
}
