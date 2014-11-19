/**
 *
 */
package com.receiptofi.repository;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.receiptofi.repository.util.AppendAdditionalFields.entityUpdate;
import static com.receiptofi.repository.util.AppendAdditionalFields.isActive;
import static com.receiptofi.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.types.DocumentStatusEnum;
import com.receiptofi.domain.value.ReceiptGrouped;
import com.receiptofi.domain.value.ReceiptGroupedByBizLocation;
import com.receiptofi.utils.DateUtil;

import org.apache.commons.lang3.StringUtils;

import org.bson.types.ObjectId;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * @author hitender
 * @since Dec 26, 2012 9:17:04 PM
 */
@Repository
public final class ReceiptManagerImpl implements ReceiptManager {
    private static final Logger LOG = LoggerFactory.getLogger(ReceiptManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            ReceiptEntity.class,
            Document.class,
            "collection");

    @Value ("${displayMonths:13}")
    private int displayMonths;

    @Autowired private MongoTemplate mongoTemplate;
    @Autowired private ItemManager itemManager;
    @Autowired private FileSystemManager fileSystemManager;
    @Autowired private StorageManager storageManager;

    @Override
    public List<ReceiptEntity> getAllObjects() {
        return mongoTemplate.findAll(ReceiptEntity.class, TABLE);
    }

    @Override
    public List<ReceiptEntity> getAllReceipts(String receiptUserId) {
        Criteria criteria = where("RID").is(receiptUserId)
                .andOperator(
                        isActive(),
                        isNotDeleted()
                );

        Sort sort = new Sort(DESC, "RTXD").and(new Sort(DESC, "C"));
        return mongoTemplate.find(query(criteria).with(sort), ReceiptEntity.class, TABLE);
    }

    @Override
    public List<ReceiptEntity> getAllReceiptsForTheYear(String receiptUserId, DateTime startOfTheYear) {
        Criteria criteria = where("RID").is(receiptUserId)
                .and("RTXD").gte(startOfTheYear)
                .andOperator(
                        isActive(),
                        isNotDeleted()
                );

        Sort sort = new Sort(DESC, "RTXD").and(new Sort(DESC, "C"));
        return mongoTemplate.find(query(criteria).with(sort), ReceiptEntity.class, TABLE);
    }

    @Override
    public List<ReceiptEntity> getAllReceiptsForThisMonth(String receiptUserId, DateTime monthYear) {
        Criteria criteria = where("RID").is(receiptUserId)
                .and("M").is(monthYear.getMonthOfYear())
                .and("Y").is(monthYear.getYear())
                .andOperator(
                        isActive(),
                        isNotDeleted()
                );

        Sort sort = new Sort(DESC, "RTXD").and(new Sort(DESC, "C"));
        return mongoTemplate.find(query(criteria).with(sort), ReceiptEntity.class, TABLE);
    }

    @Override
    public List<ReceiptEntity> getAllUpdatedReceiptSince(String receiptUserId, Date since) {
        Sort sort = new Sort(DESC, "RTXD").and(new Sort(DESC, "C"));
        return mongoTemplate.find(
                query(where("RID").is(receiptUserId).and("U").gte(since)).with(sort),
                ReceiptEntity.class,
                TABLE
        );
    }

    @Override
    public Iterator<ReceiptGrouped> getAllObjectsGroupedByDate(String receiptUserId) {
        GroupBy groupBy = GroupBy.key("T", "M", "Y")
                .initialDocument("{ total: 0 }")
                .reduceFunction("function(obj, result) { " +
                        "  result.day = obj.T; " +
                        "  result.month = obj.M; " +
                        "  result.year = obj.Y; " +
                        "  result.total += obj.TOT; " +
                        "}");

        Criteria criteria = where("RID").is(receiptUserId)
                .andOperator(
                        isActive(),
                        isNotDeleted()
                );

        GroupByResults<ReceiptGrouped> results = mongoTemplate.group(criteria, TABLE, groupBy, ReceiptGrouped.class);
        return results.iterator();
    }

    //TODO find a way to format the total in group by
    @Override
    public Iterator<ReceiptGrouped> getAllObjectsGroupedByMonth(String receiptUserId) {
        GroupBy groupBy = GroupBy.key("M", "Y")
                .initialDocument("{ total: 0 }")
                .reduceFunction("function(obj, result) { " +
                        "  result.month = obj.M; " +
                        "  result.year = obj.Y; " +
                        "  result.total += obj.TOT; " +
                        "}");

        DateTime date = DateUtil.now().minusMonths(displayMonths);
        DateTime since = new DateTime(date.getYear(), date.getMonthOfYear(), 1, 0, 0);
        Criteria criteria = where("RID").is(receiptUserId)
                .and("RTXD").gte(since.toDate())
                .andOperator(
                        isActive(),
                        isNotDeleted()
                );

        GroupByResults<ReceiptGrouped> results = mongoTemplate.group(criteria, TABLE, groupBy, ReceiptGrouped.class);
        return results.iterator();
    }

    public Iterator<ReceiptGroupedByBizLocation> getAllReceiptGroupedByBizLocation(String receiptUserId) {
        GroupBy groupBy = GroupBy.key("BIZ_STORE", "BIZ_NAME")
                .initialDocument("{ total: 0 }")
                .reduceFunction("function(obj, result) { " +
                        "  result.total += obj.TOTAL; " +
                        "  result.bizStore = obj.BIZ_STORE; " +
                        "  result.bizName = obj.BIZ_NAME; " +
                        "}");


        DateTime date = DateUtil.now().minusMonths(displayMonths);
        DateTime since = new DateTime(date.getYear(), date.getMonthOfYear(), 1, 0, 0);
        Criteria criteria = where("RID").is(receiptUserId)
                .and("RTXD").gte(since.toDate())
                .andOperator(
                        isActive(),
                        isNotDeleted()
                );

        GroupByResults<ReceiptGroupedByBizLocation> results = mongoTemplate.group(
                criteria,
                TABLE,
                groupBy,
                ReceiptGroupedByBizLocation.class);
        return results.iterator();
    }

    @Override
    public void save(ReceiptEntity object) {
        mongoTemplate.setWriteResultChecking(WriteResultChecking.LOG);
        try {
            // Cannot use insert because insert does not perform update like save.
            // Save will always try to update or create new record.
            // mongoTemplate.insert(object, TABLE);

            if (object.getId() != null) {
                object.setUpdated();
            }
            object.computeChecksum();
            mongoTemplate.save(object, TABLE);
        } catch (DataIntegrityViolationException e) {
            LOG.error("Duplicate record entry for ReceiptEntity={}", e);
            //todo should throw a better exception; this is highly likely to happen any time soon
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Use findReceipt method instead of findOne
     *
     * @param id
     * @return
     */
    @Deprecated
    @Override
    public ReceiptEntity findOne(String id) {
        return mongoTemplate.findOne(query(where("id").is(id)), ReceiptEntity.class, TABLE);
    }

    @Override
    public ReceiptEntity findOne(String receiptId, String receiptUserId) {
        Query query = query(where("id").is(receiptId)
                .and("RID").is(receiptUserId));

        return mongoTemplate.findOne(query, ReceiptEntity.class, TABLE);
    }

    /**
     * Use this method instead of findOne
     *
     * @param receiptId
     * @param receiptUserId
     * @return
     */
    @Override
    public ReceiptEntity findReceipt(String receiptId, String receiptUserId) {
        Query query = query(where("id").is(receiptId)
                        .and("RID").is(receiptUserId)
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )
        );
        return mongoTemplate.findOne(query, ReceiptEntity.class, TABLE);
    }

    @Override
    public List<ReceiptEntity> findReceipt(BizNameEntity bizNameEntity, String receiptUserId) {
        Criteria criteria = where("RID").is(receiptUserId)
                .and("BIZ_NAME.$id").is(new ObjectId(bizNameEntity.getId()))
                .andOperator(
                        isActive(),
                        isNotDeleted()
                );

        Sort sort = new Sort(DESC, "RTXD");
        return mongoTemplate.find(query(criteria).with(sort), ReceiptEntity.class, TABLE);
    }

    @Override
    public ReceiptEntity findWithReceiptOCR(String documentId) {
        Query query = query(where("DID").is(documentId));
        return mongoTemplate.findOne(query, ReceiptEntity.class, TABLE);
    }

    @Override
    public void deleteHard(ReceiptEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public void deleteSoft(ReceiptEntity object) {
        //Deleted check sum need re-calculation
        object.markAsDeleted();

        //Re-calculate check sum for deleted object
        object.computeChecksum();
        String checksum = object.getChecksum();

        if (hasRecordWithSimilarChecksum(checksum)) {
            removeCompleteReminiscenceOfSoftDeletedReceipt(checksum);
        }

        Query query = query(where("id").is(object.getId()));
        Update update = Update.update("D", true).set("CS", checksum);
        mongoTemplate.updateFirst(query, entityUpdate(update), ReceiptEntity.class);
    }

    /**
     * When a receipt is marked as soft delete receipt then previously soft deleted receipt is completely removed
     *
     * @param checksum
     */
    private void removeCompleteReminiscenceOfSoftDeletedReceipt(String checksum) {
        Criteria criteria = where("CS").is(checksum);
        List<ReceiptEntity> duplicateDeletedReceipts = mongoTemplate.find(query(criteria), ReceiptEntity.class, TABLE);
        for (ReceiptEntity receiptEntity : duplicateDeletedReceipts) {
            itemManager.deleteWhereReceipt(receiptEntity);
            fileSystemManager.deleteHard(receiptEntity.getFileSystemEntities());
            storageManager.deleteHard(receiptEntity.getFileSystemEntities());
            deleteHard(receiptEntity);
        }
    }

    @Override
    public long countAllReceiptForAStore(BizStoreEntity bizStoreEntity) {
        Criteria criteria = where("BIZ_STORE.$id").is(new ObjectId(bizStoreEntity.getId()));
        return mongoTemplate.count(query(criteria), TABLE);
    }

    @Override
    public long countAllReceiptForABizName(BizNameEntity bizNameEntity) {
        Criteria criteria = where("BIZ_NAME.$id").is(new ObjectId(bizNameEntity.getId()));
        return mongoTemplate.count(query(criteria), TABLE);
    }

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }

    @Override
    public List<ReceiptEntity> findThisDayReceipts(int year, int month, int day, String receiptUserId) {
        Criteria criteria = where("RID").is(receiptUserId)
                .and("Y").is(year)
                .and("M").is(month)
                .and("T").is(day)
                .andOperator(
                        isActive(),
                        isNotDeleted()
                );

        Sort sort = new Sort(DESC, "RTXD");
        return mongoTemplate.find(query(criteria).with(sort), ReceiptEntity.class, TABLE);
    }

    @Override
    public boolean notDeletedChecksumDuplicate(String checksum, String id) {
        //Active condition is required for re-check criteria
        return !mongoTemplate.find(checksumQueryIfDuplicateExists(checksum, id), ReceiptEntity.class, TABLE).isEmpty();
    }

    @Override
    public ReceiptEntity findNotDeletedChecksumDuplicate(String checksum, String id) {
        return mongoTemplate.findOne(checksumQueryIfDuplicateExists(checksum, id), ReceiptEntity.class, TABLE);
    }

    @Override
    public boolean hasRecordWithSimilarChecksum(String checksum) {
        return !mongoTemplate.find(checksumQuery(checksum), ReceiptEntity.class, TABLE).isEmpty();
    }

    @Override
    public void removeExpensofiFilenameReference(String filename) {
        mongoTemplate.findAndModify(
                query(where("EXF").is(filename)),
                Update.update("EXF", StringUtils.EMPTY),
                ReceiptEntity.class
        );
    }

    private Query checksumQuery(String checksum) {
        return query(where("CS").is(checksum));
    }

    /**
     * Ignore the current id receipt and see if there is another receipt with similar checksum exists
     *
     * @param checksum
     * @param id
     * @return
     */
    private Query checksumQueryIfDuplicateExists(String checksum, String id) {
        Query query = checksumQuery(checksum)
                .addCriteria(isNotDeleted()
                                .orOperator(
                                        where("DS").is(DocumentStatusEnum.TURK_REQUEST.getName()),
                                        where("DS").is(DocumentStatusEnum.TURK_PROCESSED.getName()),
                                        where("A").is(true),
                                        where("A").is(false)
                                )
                );

        if (!StringUtils.isBlank(id)) {
            //id is blank for new document; whereas for re-check id is always present
            //in such a scenario use method --> hasRecordWithSimilarChecksum
            query.addCriteria(where("id").ne(id));
        }

        return query;
    }
}
