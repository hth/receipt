/**
 *
 */
package com.receiptofi.repository;

import com.mongodb.client.result.UpdateResult;
import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.types.DocumentStatusEnum;
import com.receiptofi.domain.value.ReceiptGrouped;
import com.receiptofi.domain.value.ReceiptGroupedByBizLocation;
import com.receiptofi.domain.value.ReceiptListViewGrouped;
import com.receiptofi.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.receiptofi.repository.util.AppendAdditionalFields.*;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

/**
 * @author hitender
 * @since Dec 26, 2012 9:17:04 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class ReceiptManagerImpl implements ReceiptManager {
    private static final Logger LOG = LoggerFactory.getLogger(ReceiptManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            ReceiptEntity.class,
            Document.class,
            "collection");

    @Value ("${displayMonths:12}")
    private int displayMonths;

    private MongoTemplate mongoTemplate;
    private ItemManager itemManager;
    private FileSystemManager fileSystemManager;
    private StorageManager storageManager;

    @Autowired
    public ReceiptManagerImpl(
            ItemManager itemManager,
            FileSystemManager fileSystemManager,
            StorageManager storageManager,
            MongoTemplate mongoTemplate) {

        this.itemManager = itemManager;
        this.fileSystemManager = fileSystemManager;
        this.storageManager = storageManager;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<ReceiptEntity> getAllReceipts(String rid) {
        return mongoTemplate.find(
                query(where("RID").is(rid)
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )
                ).with(new Sort(DESC, "RTXD").and(new Sort(DESC, "C"))),
                ReceiptEntity.class,
                TABLE);
    }

    @Override
    public List<ReceiptEntity> getAllReceiptsForTheYear(String rid, DateTime startOfTheYear) {
        return mongoTemplate.find(
                query(where("RID").is(rid)
                        .and("RTXD").gte(startOfTheYear)
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )
                ).with(new Sort(DESC, "RTXD").and(new Sort(DESC, "C"))),
                ReceiptEntity.class,
                TABLE);
    }

    @Override
    public List<ReceiptEntity> getAllReceiptsForThisMonth(String rid, DateTime monthYear) {
        Criteria criteria = where("RID").is(rid)
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
    public Iterator<ReceiptGrouped> getAllObjectsGroupedByDate(String rid) {
        try {
            GroupBy groupBy = GroupBy.key("T", "M", "Y")
                    .initialDocument("{ splitTotal: 0 }")
                    .reduceFunction("function(obj, result) { " +
                            "  result.day = obj.T; " +
                            "  result.month = obj.M; " +
                            "  result.year = obj.Y; " +
                            "  result.countryShortName = obj.CS; " +
                            "  result.splitTotal += obj.ST; " +
                            "}");

            Criteria criteria = where("RID").is(rid)
                    .andOperator(
                            isActive(),
                            isNotDeleted()
                    );

            GroupByResults<ReceiptGrouped> results = mongoTemplate.group(criteria, TABLE, groupBy, ReceiptGrouped.class);
            return results.iterator();
        } catch (ConverterNotFoundException e) {
            LOG.error("Could missing element reason={}", e.getLocalizedMessage(), e);
            return null;
        } catch (Exception e) {
            LOG.error("Failed reason={}", e.getLocalizedMessage(), e);
            return null;
        }
    }

    /**
     * db.getCollection('RECEIPT').aggregate( [
     * { $match: { "RID": "10000000077" } },
     * { $group: { _id: {year : "$Y", month : "$M"}, splitTotal: { $sum: "$ST" } } },
     * { $sort: {"_id.month": 1}}
     * ] );
     * db.getCollection('RECEIPT').aggregate( [
     * { $match: { "RID": "10000000077" } },
     * { $group: { _id: {year: { $year: "$RTXD" }, month: { $month: "$RTXD" },}, splitTotal: { $sum: "$ST" } } },
     * { $sort: {"_id.month": 1}}
     * ] );
     */

    @Override
    public List<ReceiptGrouped> getReceiptGroupedByMonth(String rid) {
        DateTime date = DateUtil.now().minusMonths(displayMonths);
        DateTime since = new DateTime(date.getYear(), date.getMonthOfYear(), 1, 0, 0);
        TypedAggregation<ReceiptEntity> agg = newAggregation(ReceiptEntity.class,
                match(where("RID").is(rid)
                        .and("RTXD").gte(since.toDate())
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )),
                group("year", "month")
                        .first("year").as("Y")
                        .first("month").as("M")
                        .first("countryShortName").as("countryShortName")
                        .sum("splitTotal").as("splitTotal"),
                sort(DESC, previousOperation())
        );

        return mongoTemplate.aggregate(agg, TABLE, ReceiptGrouped.class).getMappedResults();
    }

    public List<ReceiptListViewGrouped> getReceiptForGroupedByMonth(String rid, int month, int year) {
        return mongoTemplate.find(
                query(where("RID").is(rid)
                        .and("M").is(month)
                        .and("Y").is(year)
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )
                ).with(new Sort(DESC, "RTXD")),
                ReceiptListViewGrouped.class,
                TABLE
        );
    }

    public Iterator<ReceiptGroupedByBizLocation> getAllReceiptGroupedByBizLocation(String rid) {
        GroupBy groupBy = GroupBy.key("BIZ_STORE", "BIZ_NAME")
                .initialDocument("{ splitTotal: 0 }")
                .reduceFunction("function(obj, result) { " +
                        "  result.splitTotal += obj.ST; " +
                        "  result.bizStore = obj.BIZ_STORE; " +
                        "  result.bizName = obj.BIZ_NAME; " +
                        "}");


        DateTime date = DateUtil.now().minusMonths(displayMonths);
        DateTime since = new DateTime(date.getYear(), date.getMonthOfYear(), 1, 0, 0);
        Criteria criteria = where("RID").is(rid)
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

    @Override
    public ReceiptEntity getReceipt(String id, String rid) {
        Assert.hasText(id, "Id is empty");
        return mongoTemplate.findOne(query(where("id").is(id).and("RID").is(rid)), ReceiptEntity.class, TABLE);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public ReceiptEntity findReceipt(String id) {
        Assert.hasText(id, "Id is empty");
        return mongoTemplate.findOne(
                query(where("id").is(id)
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )
                ),
                ReceiptEntity.class,
                TABLE);
    }

    /**
     * @param id
     * @param rid
     * @return
     */
    @Override
    public ReceiptEntity findReceipt(String id, String rid) {
        Assert.hasText(id, "Id is empty");
        return mongoTemplate.findOne(
                query(where("id").is(id).and("RID").is(rid)
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )
                ),
                ReceiptEntity.class,
                TABLE);
    }

    /**
     * Use this method when doing recheck.
     *
     * @param id
     * @param rid
     * @return
     */
    @Override
    public ReceiptEntity findReceiptWhileRecheck(String id, String rid) {
        Assert.hasText(id, "Id is empty");
        return mongoTemplate.findOne(
                query(where("id").is(id).and("RID").is(rid)
                        .andOperator(
                                isNotActive(),
                                isNotDeleted()
                        )
                ),
                ReceiptEntity.class,
                TABLE);
    }

    /**
     * @param id
     * @param rid
     * @return
     */
    @Override
    public ReceiptEntity findReceiptForMobile(String id, String rid) {
        Assert.hasText(id, "Id is empty");
        return mongoTemplate.findOne(
                query(where("id").is(id).and("RID").is(rid)),
                ReceiptEntity.class,
                TABLE);
    }

    @Override
    public List<ReceiptEntity> findReceipt(BizNameEntity bizNameEntity, String receiptUserId, DateTime receiptForMonth) {
        Criteria criteria = where("RID").is(receiptUserId)
                .and("BIZ_NAME.$id").is(new ObjectId(bizNameEntity.getId()))
                .and("RTXD").gte(receiptForMonth).lte(receiptForMonth.plusMonths(1).withTimeAtStartOfDay().minusMillis(1))
                .andOperator(
                        isActive(),
                        isNotDeleted()
                );

        Sort sort = new Sort(DESC, "RTXD");
        return mongoTemplate.find(query(criteria).with(sort), ReceiptEntity.class, TABLE);
    }

    @Override
    public ReceiptEntity findWithReceiptOCR(String documentId) {
        return mongoTemplate.findOne(query(where("DID").is(documentId)), ReceiptEntity.class, TABLE);
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

        mongoTemplate.updateFirst(
                query(where("id").is(object.getId())),
                entityUpdate(update("D", true).set("CZ", checksum)),
                ReceiptEntity.class);
    }

    /**
     * When a receipt is marked as soft delete receipt then previously soft deleted receipt is completely removed.
     *
     * @param checksum
     */
    private void removeCompleteReminiscenceOfSoftDeletedReceipt(String checksum) {
        List<ReceiptEntity> duplicateDeletedReceipts = mongoTemplate.find(
                query(where("CZ").is(checksum)),
                ReceiptEntity.class,
                TABLE);

        for (ReceiptEntity receiptEntity : duplicateDeletedReceipts) {
            itemManager.deleteWhereReceipt(receiptEntity);
            fileSystemManager.deleteHard(receiptEntity.getFileSystemEntities());
            storageManager.deleteHard(receiptEntity.getFileSystemEntities());
            deleteHard(receiptEntity);
        }
    }

    @Override
    public long countAllReceiptForAStore(BizStoreEntity bizStoreEntity) {
        return mongoTemplate.count(query(where("BIZ_STORE.$id").is(new ObjectId(bizStoreEntity.getId()))), TABLE);
    }

    @Override
    public long countAllReceiptForABizName(BizNameEntity bizNameEntity) {
        return mongoTemplate.count(query(where("BIZ_NAME.$id").is(new ObjectId(bizNameEntity.getId()))), TABLE);
    }

    @Override
    public long collectionSize() {
        return mongoTemplate.getCollection(TABLE).count();
    }

    @Override
    public void updateReceiptCSWhenStoreUpdated(String countryShortName, String bizStoreId) {
        UpdateResult updateResult = mongoTemplate.updateMulti(
                query(where("BIZ_STORE.$id").is(new ObjectId(bizStoreId))),
                entityUpdate(update("CS", countryShortName)),
                ReceiptEntity.class);

        LOG.debug("Updated records count={} ack={}", updateResult.getModifiedCount(), updateResult.wasAcknowledged());
    }

    @Override
    public List<ReceiptEntity> findThisDayReceipts(int year, int month, int day, String receiptUserId) {
        return mongoTemplate.find(
                query(where("RID").is(receiptUserId)
                        .and("Y").is(year)
                        .and("M").is(month)
                        .and("T").is(day)
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )
                ).with(new Sort(DESC, "RTXD")),
                ReceiptEntity.class,
                TABLE);
    }

    @Override
    public boolean notDeletedChecksumDuplicate(String checksum, String id) {
        //Active condition is required for re-check criteria
        return !mongoTemplate.find(checksumQueryIfDuplicateExists(checksum, id), ReceiptEntity.class, TABLE).isEmpty();
    }

    @Override
    public boolean hasRecordWithSimilarChecksum(String checksum) {
        return !mongoTemplate.find(checksumQuery(checksum).addCriteria(isActive()), ReceiptEntity.class, TABLE).isEmpty();
    }

    @Override
    public ReceiptEntity findOne(String rid, Date receiptDate, Double total) {
        return mongoTemplate.findOne(
                query(where("RID").is(rid).and("RTXD").is(receiptDate).and("TOT").is(total)),
                ReceiptEntity.class
        );
    }

    @Override
    public boolean upsertExpensofiFilenameReference(String id, String filename) {
        UpdateResult updateResult = mongoTemplate.updateFirst(
                query(where("id").is(id)),
                entityUpdate(new Update().set("EXF", filename)),
                ReceiptEntity.class
        );

        return updateResult.getModifiedCount() > 0;
    }

    @Override
    public void removeExpensofiFilenameReference(String filename) {
        mongoTemplate.findAndModify(
                query(where("EXF").is(filename)),
                entityUpdate(new Update().unset("EXF")),
                ReceiptEntity.class
        );
    }

    private Query checksumQuery(String checksum) {
        return query(where("CZ").is(checksum));
    }

    /**
     * Ignore the current id receipt and see if there is another receipt with similar checksum exists.
     *
     * @param checksum
     * @param id
     * @return
     */
    private Query checksumQueryIfDuplicateExists(String checksum, String id) {
        Query query = checksumQuery(checksum)
                .addCriteria(isNotDeleted()
                        .orOperator(
                                where("DS").is(DocumentStatusEnum.REPROCESS.name()),
                                where("DS").is(DocumentStatusEnum.PROCESSED.name()),
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

    @Override
    public List<ReceiptEntity> findAllReceipts(String rid) {
        return mongoTemplate.find(query(where("RID").is(rid)), ReceiptEntity.class);
    }

    @Override
    public long countReceiptsUsingExpenseType(String expenseTypeId, String rid) {
        return mongoTemplate.count(
                query(where("EXPENSE_TAG.$id").is(new ObjectId(expenseTypeId))
                        .and("RID").is(rid)
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )
                ),
                ReceiptEntity.class
        );
    }

    @Override
    public boolean removeExpenseTagReferences(String rid, String expenseTagId) {
        return mongoTemplate.updateMulti(
                query(where("RID").is(rid).and("EXPENSE_TAG.$id").is(new ObjectId(expenseTagId))),
                entityUpdate(new Update().unset("EXPENSE_TAG")),
                TABLE
        ).getModifiedCount() > 0;
    }

    @Override
    public boolean updateFriendReceipt(String referToReceiptId, int splitCount, Double splitTotal, Double splitTax) {
        return mongoTemplate.updateMulti(
                query(where("RF").is(referToReceiptId)
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )
                ),
                entityUpdate(update("SC", splitCount).set("ST", splitTotal).set("SX", splitTax)),
                TABLE
        ).getModifiedCount() > 0;
    }

    @Override
    public boolean softDeleteFriendReceipt(String receiptId, String rid) {
        return mongoTemplate.updateFirst(
                query(where("RF").is(receiptId).and("RID").is(rid)),
                entityUpdate(update("A", false).set("D", true)),
                TABLE
        ).getModifiedCount() > 0;
    }

    @Override
    public boolean increaseSplitCount(String receiptId, double splitTotal, double splitTax) {
        return mongoTemplate.updateFirst(
                query(where("id").is(receiptId)),
                entityUpdate(update("ST", splitTotal).set("SX", splitTax).inc("SC", 1)),
                ReceiptEntity.class
        ).wasAcknowledged();
    }

    @Override
    public boolean decreaseSplitCount(String receiptId, double splitTotal, double splitTax) {
        return mongoTemplate.updateFirst(
                query(where("id").is(receiptId)),
                entityUpdate(update("ST", splitTotal).set("SX", splitTax).inc("SC", -1)),
                ReceiptEntity.class
        ).wasAcknowledged();
    }

    @Override
    public List<ReceiptEntity> getReceiptsWithoutQC() {
        return mongoTemplate.find(
                query(new Criteria()
                        .orOperator(
                                where("QC").exists(false),
                                where("QC").is(false)
                        )
                        .andOperator(
                                new Criteria()
                                        .orOperator(
                                                where("RF").exists(false),
                                                where("RF").is("")
                                        ),
                                isActive(),
                                isNotDeleted()
                        )
                ),
                ReceiptEntity.class,
                TABLE);
    }

    @Override
    public List<ReceiptEntity> findAllReceiptWithMatchingReferReceiptId(String receiptId) {
        return mongoTemplate.find(
                query(where("RF").is(receiptId)
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )
                ),
                ReceiptEntity.class,
                TABLE);
    }

    @Override
    public List<ReceiptEntity> getReceiptsWithExpenseTags(String rid, List<ObjectId> expenseTags, int delayDuration) {
        if (0 < expenseTags.size()) {
            return mongoTemplate.find(
                    query(where("RID").is(rid)
                            .and("RTXD").lt(DateUtil.getDateMinusDay(delayDuration))
                            .and("EXPENSE_TAG.$id").in(expenseTags)
                            .andOperator(
                                    isActive(),
                                    isNotDeleted()
                            )
                    ),
                    ReceiptEntity.class,
                    TABLE);
        }

        return new ArrayList<>();
    }
}
