/**
 *
 */
package com.receiptofi.repository;

import static com.receiptofi.repository.util.AppendAdditionalFields.isActive;
import static com.receiptofi.repository.util.AppendAdditionalFields.isDeleted;
import static com.receiptofi.repository.util.AppendAdditionalFields.isNotActive;
import static com.receiptofi.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

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
        "PMD.MethodArgumentCouldBeFinal"
})
@Repository
public final class DocumentManagerImpl implements DocumentManager {
    private static final Logger LOG = LoggerFactory.getLogger(ReceiptManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            DocumentEntity.class,
            Document.class,
            "collection");

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<DocumentEntity> getAllObjects() {
        return mongoTemplate.findAll(DocumentEntity.class, TABLE);
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
    public DocumentEntity findOne(String id) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public DocumentEntity findOne(String id, String receiptUserId) {
        return mongoTemplate.findOne(
                query(where("id").is(id).and("RID").is(receiptUserId)),
                DocumentEntity.class,
                TABLE
        );
    }

    @Override
    public DocumentEntity findActiveOne(String id) {
        return mongoTemplate.findOne(
                query(where("id").is(id)).addCriteria(isActive()),
                DocumentEntity.class,
                TABLE
        );
    }

    @Override
    public DocumentEntity findRejectedOne(String id) {
        return mongoTemplate.findOne(
                query(where("id").is(id).and("DS").is(DocumentStatusEnum.TURK_RECEIPT_REJECT)),
                DocumentEntity.class,
                TABLE
        );
    }

    @Override
    public void deleteHard(DocumentEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public long numberOfPendingReceipts(String receiptUserId) {
        return mongoTemplate.count(
                query(where("RID").is(receiptUserId))
                        .addCriteria(isActive())
                        .addCriteria(isNotDeleted()),
                TABLE
        );
    }

    @Override
    public long numberOfRejectedReceipts(String receiptUserId) {
        return mongoTemplate.count(
                query(where("RID").is(receiptUserId).and("DS").is(DocumentStatusEnum.TURK_RECEIPT_REJECT))
                        .addCriteria(isNotActive())
                        .addCriteria(isDeleted()),
                TABLE
        );
    }

    @Override
    public List<DocumentEntity> getAllPending(String receiptUserId) {
        return mongoTemplate.find(
                query(where("RID").is(receiptUserId))
                        .addCriteria(isActive())
                        .addCriteria(isNotDeleted())
                        .with(new Sort(Direction.ASC, "C")),
                DocumentEntity.class,
                TABLE
        );
    }

    @Override
    public List<DocumentEntity> getAllRejected(String receiptUserId) {
        return mongoTemplate.find(
                query(where("RID").is(receiptUserId).and("DS").is(DocumentStatusEnum.TURK_RECEIPT_REJECT))
                        .addCriteria(isNotActive())
                        .addCriteria(isDeleted())
                        .with(new Sort(Direction.ASC, "C")),
                DocumentEntity.class,
                TABLE
        );
    }

    @Override
    public List<DocumentEntity> getAllRejected(int purgeRejectedDocumentAfterDay) {
        return mongoTemplate.find(
                query(
                        where("DS").is(DocumentStatusEnum.TURK_RECEIPT_REJECT)
                                .and("U").lte(DateTime.now().minusDays(purgeRejectedDocumentAfterDay)
                        )
                )
                        .addCriteria(isNotActive())
                        .addCriteria(isDeleted()),
                DocumentEntity.class,
                TABLE
        );
    }

    @Override
    public long getTotalPending() {
        return mongoTemplate.count(
                query(
                        where("DS").is(DocumentStatusEnum.OCR_PROCESSED)
                ).addCriteria(isNotDeleted()),
                DocumentEntity.class
        );
    }

    @Override
    public long getTotalProcessedToday() {
        return mongoTemplate.count(
                query(
                        where("DS").ne(DocumentStatusEnum.OCR_PROCESSED)
                                .and("U").gte(DateUtil.midnight(new Date()))
                ).addCriteria(isNotDeleted()),
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
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }
}
